package com.oems.dto;

import com.oems.model.OptionChoice;

public record QuestionResponse(
        String id,
        String questionText,
        String optionA,
        String optionB,
        String optionC,
        String optionD,
        OptionChoice correctOption,
        Integer marks,
        Integer orderIndex
) {
}
