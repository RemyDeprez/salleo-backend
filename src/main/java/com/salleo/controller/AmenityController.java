package com.salleo.controller;

import com.salleo.entity.Amenity;
import com.salleo.service.AmenityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/amenities")
@RequiredArgsConstructor
public class AmenityController {
    private final AmenityService service;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Amenity>> list() { return ResponseEntity.ok(service.findAll()); }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Amenity> get(@PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Amenity> create(@RequestBody Amenity b) {
        Amenity created = service.save(b);
        return ResponseEntity.created(URI.create("/api/amenities/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Amenity> update(@PathVariable Long id, @RequestBody Amenity payload) {
        return service.findById(id).map(existing -> {
            payload.setId(id);
            return ResponseEntity.ok(service.save(payload));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) { service.deleteById(id); return ResponseEntity.noContent().build(); }
}

