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
@Document(collection = "exam_attempts")
public class ExamAttempt {

    @Id
    private String id;

    @DBRef
    private User student;

    @DBRef
    private Exam exam;

    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;
    private LocalDateTime deadline;

    private Integer score;
    private Integer totalMarks;
    private Boolean passed;

    private AttemptStatus status;
    private String questionOrder;
}
