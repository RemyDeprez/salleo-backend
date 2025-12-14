package com.salleo.service.impl;

import com.salleo.dto.UserDto;
import com.salleo.entity.User;
import com.salleo.exception.ResourceNotFoundException;
import com.salleo.repository.UserRepository;
import com.salleo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private UserDto toDto(User u) {
        return UserDto.builder()
                .id(u.getId())
                .username(u.getUsername())
                .email(u.getEmail())
                .role(u.getRole())
                .build();
    }

    private User toEntity(UserDto dto) {
        User u = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .role(dto.getRole() == null ? "PLAYER" : dto.getRole())
                .build();
        if (dto.getPasswordHash() != null && !dto.getPasswordHash().isBlank()) {
            String raw = dto.getPasswordHash().trim();
            u.setPassword(passwordEncoder.encode(raw));
        }
        return u;
    }

    @Override
    public UserDto create(UserDto userDto) {
        User u = toEntity(userDto);
        User saved = userRepository.save(u);
        return toDto(saved);
    }

    @Override
    public UserDto getById(Long id) {
        User u = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return toDto(u);
    }

    @Override
    public Page<UserDto> list(Pageable pageable) {
        var page = userRepository.findAll(pageable);
        List<UserDto> dtos = page.stream().map(this::toDto).collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        User u = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (userDto.getUsername() != null) u.setUsername(userDto.getUsername());
        if (userDto.getEmail() != null) u.setEmail(userDto.getEmail());
        if (userDto.getRole() != null) u.setRole(userDto.getRole());
        if (userDto.getPasswordHash() != null && !userDto.getPasswordHash().isBlank()) {
            String raw = userDto.getPasswordHash().trim();
            u.setPassword(passwordEncoder.encode(raw));
        }
        User updated = userRepository.save(u);
        return toDto(updated);
    }

    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) throw new ResourceNotFoundException("User not found");
        userRepository.deleteById(id);
    }

    // Migration helper left for backward compatibility but no longer used by login flow
    public void migratePasswordToBcryptSha(User user, String providedPlainPassword) {
        // kept as no-op migration helper for now; prefer manual migration locally
        if (user == null || providedPlainPassword == null) return;
        String bcrypt = passwordEncoder.encode(providedPlainPassword.trim());
        user.setPassword(bcrypt);
        userRepository.save(user);
    }
}
