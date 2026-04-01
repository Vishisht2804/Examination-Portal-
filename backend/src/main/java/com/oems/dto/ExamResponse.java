package com.oems.dto;

import java.time.LocalDateTime;

public record ExamResponse(
        String id,
        String title,
        String description,
        String courseId,
        String courseName,
        String teacherId,
        String teacherName,
        Integer durationMinutes,
        Integer totalMarks,
        LocalDateTime scheduledStart,
        LocalDateTime scheduledEnd,
        boolean published,
        boolean randomizeQuestions,
        Integer passPercentage,
        long questionCount,
        LocalDateTime createdAt
) {
}
