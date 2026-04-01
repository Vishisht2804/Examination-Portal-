package com.oems.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "student_answers")
public class StudentAnswer {

    @Id
    private String id;

    @DBRef
    private ExamAttempt attempt;

    @DBRef
    private Question question;

    private OptionChoice selectedOption;
    private Boolean correct;
    private Integer marksAwarded;
}
