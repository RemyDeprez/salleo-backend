package com.salleo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "recurring_reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecurringReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "room_id")
    private Room room;

    @Column(columnDefinition = "text")
    private String rule; // iCal RRULE

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "next_run")
    private Instant nextRun;

    @Column(name = "created_at")
    private Instant createdAt;
}

