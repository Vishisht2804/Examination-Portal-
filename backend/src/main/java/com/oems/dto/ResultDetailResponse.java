package com.oems.dto;

import com.oems.model.AttemptStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ResultDetailResponse(
        String attemptId,
        String examId,
        String examTitle,
        Integer score,
        Integer totalMarks,
        double percentage,
        boolean passed,
        AttemptStatus status,
        LocalDateTime startedAt,
        LocalDateTime submittedAt,
        List<QuestionResultResponse> questions
) {
}
