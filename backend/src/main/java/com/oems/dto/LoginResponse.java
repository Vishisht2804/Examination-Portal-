package com.oems.dto;

import com.oems.model.Role;

public record LoginResponse(
        String token,
        String userId,
        Role role,
        String fullName
) {
}
