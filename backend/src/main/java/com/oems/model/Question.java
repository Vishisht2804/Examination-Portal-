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
@Document(collection = "questions")
public class Question {

    @Id
    private String id;

    @DBRef
    private Exam exam;

    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;

    private OptionChoice correctOption;

    @Builder.Default
    private Integer marks = 1;

    private Integer orderIndex;
}
