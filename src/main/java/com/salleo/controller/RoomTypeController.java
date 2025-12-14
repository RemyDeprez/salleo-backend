package com.salleo.controller;

import com.salleo.entity.RoomType;
import com.salleo.service.RoomTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/room-types")
@RequiredArgsConstructor
public class RoomTypeController {
    private final RoomTypeService service;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RoomType>> list() { return ResponseEntity.ok(service.findAll()); }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RoomType> get(@PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoomType> create(@RequestBody RoomType b) {
        RoomType created = service.save(b);
        return ResponseEntity.created(URI.create("/api/room-types/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoomType> update(@PathVariable Long id, @RequestBody RoomType payload) {
        return service.findById(id).map(existing -> {
            payload.setId(id);
            return ResponseEntity.ok(service.save(payload));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) { service.deleteById(id); return ResponseEntity.noContent().build(); }
}

