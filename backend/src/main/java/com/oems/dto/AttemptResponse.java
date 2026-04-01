package com.oems.dto;

import com.oems.model.AttemptStatus;

import java.time.LocalDateTime;
import java.util.List;

public record AttemptResponse(
        String attemptId,
        String examId,
        String examTitle,
        AttemptStatus status,
        LocalDateTime startedAt,
        LocalDateTime deadline,
        long remainingSeconds,
        List<StudentQuestionResponse> questions,
        List<AnswerStateResponse> answers
) {
}
