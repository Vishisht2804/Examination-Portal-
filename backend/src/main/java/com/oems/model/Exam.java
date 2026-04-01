package com.oems.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "exams")
public class Exam {

    @Id
    private String id;

    private String title;
    private String description;

    @DBRef
    private Course course;

    @DBRef
    private User teacher;

    private Integer durationMinutes;

    @Builder.Default
    private Integer totalMarks = 0;

    private LocalDateTime scheduledStart;
    private LocalDateTime scheduledEnd;

    @Builder.Default
    private boolean published = false;

    @Builder.Default
    private boolean randomizeQuestions = true;

    @Builder.Default
    private Integer passPercentage = 40;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
