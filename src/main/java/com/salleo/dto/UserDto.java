package com.salleo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;

    @NotBlank
    @Size(min = 3, max = 100)
    private String username;

    @Size(min = 6, max = 255)
    private String passwordHash; // write-only in API clients (optional on update)

    @Email
    private String email;

    private String role;
}

