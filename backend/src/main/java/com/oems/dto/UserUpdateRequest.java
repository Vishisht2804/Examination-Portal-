package com.oems.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserUpdateRequest(
        @NotBlank String fullName,
        @Email @NotBlank String email,
        @NotNull Boolean active
) {
}
