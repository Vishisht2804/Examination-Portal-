package com.oems.dto;

import com.oems.model.AttemptStatus;

public record SubmitResultResponse(
        String attemptId,
        AttemptStatus status,
        Integer score,
        Integer totalMarks,
        double percentage,
        boolean passed
) {
}
