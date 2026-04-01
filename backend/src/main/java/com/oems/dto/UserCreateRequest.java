package com.oems.dto;

import com.oems.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserCreateRequest(
        @NotBlank String username,
        @Email @NotBlank String email,
        @NotBlank String password,
        @NotBlank String fullName,
        @NotNull Role role
) {
}
