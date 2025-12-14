package com.salleo.service;

import com.salleo.entity.Reservation;
import com.salleo.repository.ReservationRepository;
import org.springframework.stereotype.Service;

@Service
public class ReservationService extends BaseCrudService<Reservation> {
    public ReservationService(ReservationRepository repo) { super(repo); }
}

