package com.oems.dto;

import jakarta.validation.constraints.NotNull;

public record ReassignTeacherRequest(
        @NotNull String teacherId
) {
}
