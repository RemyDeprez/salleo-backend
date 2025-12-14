-- schema.sql
-- PostgreSQL schema for Salleo (rooms & reservations)
-- This script creates tables, types and constraints for a meeting/room reservation system.

-- Required extension for exclusion constraints on ranges
CREATE EXTENSION IF NOT EXISTS btree_gist;

-- ENUM types
DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'reservation_status') THEN
        CREATE TYPE reservation_status AS ENUM ('PENDING', 'CONFIRMED', 'CANCELLED');
    END IF;
END $$;

-- Users table (match Java entity `users`)
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Buildings (optional grouping for rooms)
CREATE TABLE IF NOT EXISTS buildings (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL UNIQUE,
    address TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Rooms
CREATE TABLE IF NOT EXISTS rooms (
    id BIGSERIAL PRIMARY KEY,
    building_id BIGINT REFERENCES buildings(id) ON DELETE SET NULL,
    name VARCHAR(200) NOT NULL,
    code VARCHAR(100),
    capacity INT NOT NULL DEFAULT 1,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT rooms_unique_name_per_building UNIQUE (building_id, name)
);

-- Room types (e.g., Meeting, Conference, Huddle)
CREATE TABLE IF NOT EXISTS room_types (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

-- Room to type relation (optional one-to-many via FK)
ALTER TABLE rooms
    ADD COLUMN IF NOT EXISTS room_type_id BIGINT REFERENCES room_types(id) ON DELETE SET NULL;

-- Amenities
CREATE TABLE IF NOT EXISTS amenities (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

-- Junction table room_amenities
CREATE TABLE IF NOT EXISTS room_amenities (
    room_id BIGINT NOT NULL REFERENCES rooms(id) ON DELETE CASCADE,
    amenity_id BIGINT NOT NULL REFERENCES amenities(id) ON DELETE CASCADE,
    PRIMARY KEY (room_id, amenity_id)
);

-- Reservations
-- We use tstzrange + exclusion constraint to avoid overlapping reservations for the same room.
CREATE TABLE IF NOT EXISTS reservations (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    room_id BIGINT NOT NULL REFERENCES rooms(id) ON DELETE CASCADE,
    start_ts TIMESTAMPTZ NOT NULL,
    end_ts TIMESTAMPTZ NOT NULL,
    period tstzrange GENERATED ALWAYS AS (tstzrange(start_ts, end_ts, '[]')) STORED,
    title VARCHAR(250),
    description TEXT,
    status reservation_status NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT reservations_no_past_times CHECK (start_ts < end_ts)
);

-- Exclusion constraint to prevent overlapping reservations for the same room
CREATE INDEX IF NOT EXISTS reservations_room_period_gist_idx ON reservations USING GIST (room_id, period);

ALTER TABLE reservations
    ADD CONSTRAINT reservations_no_overlap EXCLUDE USING GIST (room_id WITH =, period WITH &&);

-- Recurring reservations (simple representation)
CREATE TABLE IF NOT EXISTS recurring_reservations (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    room_id BIGINT NOT NULL REFERENCES rooms(id) ON DELETE CASCADE,
    rule TEXT NOT NULL, -- e.g. iCal RRULE
    start_date DATE NOT NULL,
    end_date DATE,
    next_run TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Audit / activity log (minimal)
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGSERIAL PRIMARY KEY,
    entity_type VARCHAR(100) NOT NULL,
    entity_id BIGINT,
    action VARCHAR(50) NOT NULL,
    performed_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
    details JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Useful indexes
CREATE INDEX IF NOT EXISTS idx_reservations_room_start_end ON reservations (room_id, start_ts, end_ts);
CREATE INDEX IF NOT EXISTS idx_rooms_building ON rooms (building_id);
CREATE INDEX IF NOT EXISTS idx_users_username ON users (username);

-- Trigger to update `updated_at` timestamps on update for tables that have updated_at
CREATE OR REPLACE FUNCTION touch_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Attach triggers
DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'users_touch_updated_at') THEN
        CREATE TRIGGER users_touch_updated_at
        BEFORE UPDATE ON users
        FOR EACH ROW EXECUTE FUNCTION touch_updated_at();
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'rooms_touch_updated_at') THEN
        CREATE TRIGGER rooms_touch_updated_at
        BEFORE UPDATE ON rooms
        FOR EACH ROW EXECUTE FUNCTION touch_updated_at();
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'reservations_touch_updated_at') THEN
        CREATE TRIGGER reservations_touch_updated_at
        BEFORE UPDATE ON reservations
        FOR EACH ROW EXECUTE FUNCTION touch_updated_at();
    END IF;
END $$;

-- Small seed (optional) -- create an admin user if not exists
INSERT INTO users (username, password, email, role)
SELECT 'admin', 'changeme', 'admin@example.com', 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

-- End of schema

