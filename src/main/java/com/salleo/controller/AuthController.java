package com.salleo.controller;

import com.salleo.dto.LoginRequest;
import com.salleo.dto.UserDto;
import com.salleo.entity.User;
import com.salleo.exception.ResourceNotFoundException;
import com.salleo.repository.UserRepository;
import com.salleo.security.JwtUtil;
import com.salleo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @SuppressWarnings("unused")
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDto dto) {
        // Ensure password is provided and meets minimum requirements for registration
        if (dto.getPasswordHash() == null || dto.getPasswordHash().isBlank() || dto.getPasswordHash().length() < 6) {
            return ResponseEntity.badRequest().body("Password is required and must be at least 6 characters long.");
        }

        UserDto created = userService.create(dto);
        return ResponseEntity.ok(created);
    }

    @SuppressWarnings("unused")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        var maybe = userRepository.findByEmailIgnoreCase(request.getEmail());
        if (maybe.isEmpty()) maybe = userRepository.findByUsername(request.getEmail());
        User user = maybe.orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getHashedPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("role", user.getRole());
        String token = jwtUtil.generateToken(String.valueOf(user.getId()), claims);

        return ResponseEntity.ok(Map.of("token", token, "username", user.getUsername(), "role", user.getRole()));
    }
}
