package com.oems.dto;

import com.oems.model.AttemptStatus;

import java.time.LocalDateTime;

public record ExamResultItemResponse(
        String attemptId,
        String studentId,
        String studentName,
        Integer score,
        Integer totalMarks,
        boolean passed,
        AttemptStatus status,
        LocalDateTime startedAt,
        LocalDateTime submittedAt
) {
}
