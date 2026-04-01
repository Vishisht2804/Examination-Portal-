package com.oems.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CourseResponse(
        String id,
        String name,
        String description,
        String teacherId,
        String teacherName,
        boolean active,
        LocalDateTime createdAt,
        List<String> studentIds
) {
}
