package com.salleo.controller;

import com.salleo.entity.RecurringReservation;
import com.salleo.service.RecurringReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/recurring-reservations")
@RequiredArgsConstructor
public class RecurringReservationController {
    private final RecurringReservationService service;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RecurringReservation>> list() { return ResponseEntity.ok(service.findAll()); }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RecurringReservation> get(@PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RecurringReservation> create(@RequestBody RecurringReservation r) {
        RecurringReservation created = service.save(r);
        return ResponseEntity.created(URI.create("/api/recurring-reservations/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RecurringReservation> update(@PathVariable Long id, @RequestBody RecurringReservation payload) {
        return service.findById(id).map(existing -> { payload.setId(id); return ResponseEntity.ok(service.save(payload)); })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) { service.deleteById(id); return ResponseEntity.noContent().build(); }
}

