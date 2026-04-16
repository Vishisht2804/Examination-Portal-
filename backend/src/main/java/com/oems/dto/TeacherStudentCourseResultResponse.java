package com.oems.dto;

import com.oems.model.AttemptStatus;

import java.time.LocalDateTime;

public record TeacherStudentCourseResultResponse(
        String attemptId,
        String examId,
        String examTitle,
        Integer score,
        Integer totalMarks,
        boolean passed,
        AttemptStatus status,
        LocalDateTime startedAt,
        LocalDateTime submittedAt
) {
}
