package com.oems.dto;

import com.oems.model.Role;

import java.time.LocalDateTime;

public record UserResponse(
        String id,
        String username,
        String email,
        String fullName,
        Role role,
        boolean active,
        LocalDateTime createdAt
) {
}
