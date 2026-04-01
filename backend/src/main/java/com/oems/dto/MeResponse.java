package com.oems.dto;

import com.oems.model.Role;

public record MeResponse(
        String id,
        String username,
        String email,
        String fullName,
        Role role,
        boolean active
) {
}
