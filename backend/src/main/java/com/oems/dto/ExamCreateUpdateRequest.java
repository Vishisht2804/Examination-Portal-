package com.oems.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ExamCreateUpdateRequest(
        @NotBlank String title,
        String description,
        @NotNull String courseId,
        @NotNull @Min(1) Integer durationMinutes,
        @NotNull LocalDateTime scheduledStart,
        @NotNull LocalDateTime scheduledEnd,
        @NotNull Boolean randomizeQuestions,
        @NotNull @Min(1) @Max(100) Integer passPercentage
) {
}
