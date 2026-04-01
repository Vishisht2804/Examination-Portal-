package com.oems.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CourseCreateRequest(
        @NotBlank String name,
        String description,
        @NotNull String teacherId
) {
}
