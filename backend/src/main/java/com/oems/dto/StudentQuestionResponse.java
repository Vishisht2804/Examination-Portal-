package com.oems.dto;

public record StudentQuestionResponse(
        String id,
        String questionText,
        String optionA,
        String optionB,
        String optionC,
        String optionD,
        Integer marks,
        Integer orderIndex
) {
}
