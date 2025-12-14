package com.salleo.repository;

import com.salleo.entity.RecurringReservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecurringReservationRepository extends JpaRepository<RecurringReservation, Long> {
}

