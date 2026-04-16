package com.oems.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CourseUpdateRequest(
        @NotBlank String name,
        String description,
        @NotNull String teacherId,
        @NotNull Boolean active
) {
}
