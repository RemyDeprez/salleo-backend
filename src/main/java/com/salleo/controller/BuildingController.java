package com.salleo.controller;

import com.salleo.entity.Building;
import com.salleo.service.BuildingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/buildings")
@RequiredArgsConstructor
public class BuildingController {
    private final BuildingService service;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Building>> list() { return ResponseEntity.ok(service.findAll()); }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Building> get(@PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Building> create(@RequestBody Building b) {
        Building created = service.save(b);
        return ResponseEntity.created(URI.create("/api/buildings/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Building> update(@PathVariable Long id, @RequestBody Building payload) {
        return service.findById(id).map(existing -> {
            payload.setId(id);
            return ResponseEntity.ok(service.save(payload));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) { service.deleteById(id); return ResponseEntity.noContent().build(); }
}

