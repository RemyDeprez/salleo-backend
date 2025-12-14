package com.salleo.controller;

import com.salleo.entity.Room;
import com.salleo.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService service;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Room>> list() { return ResponseEntity.ok(service.findAll()); }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Room> get(@PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Room> create(@RequestBody Room r) {
        Room created = service.save(r);
        return ResponseEntity.created(URI.create("/api/rooms/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Room> update(@PathVariable Long id, @RequestBody Room payload) {
        return service.findById(id).map(existing -> { payload.setId(id); return ResponseEntity.ok(service.save(payload)); })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) { service.deleteById(id); return ResponseEntity.noContent().build(); }
}

