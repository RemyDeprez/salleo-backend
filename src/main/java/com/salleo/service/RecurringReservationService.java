package com.salleo.service;

import com.salleo.entity.RecurringReservation;
import com.salleo.repository.RecurringReservationRepository;
import org.springframework.stereotype.Service;

@Service
public class RecurringReservationService extends BaseCrudService<RecurringReservation> {
    public RecurringReservationService(RecurringReservationRepository repo) { super(repo); }
}

