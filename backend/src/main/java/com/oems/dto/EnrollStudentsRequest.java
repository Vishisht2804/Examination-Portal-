package com.oems.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record EnrollStudentsRequest(
        @NotEmpty List<String> studentIds
) {
}
