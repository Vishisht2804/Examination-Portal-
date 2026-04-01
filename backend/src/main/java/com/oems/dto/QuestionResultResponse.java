package com.oems.dto;

import com.oems.model.OptionChoice;

public record QuestionResultResponse(
        String questionId,
        String questionText,
        OptionChoice selectedOption,
        OptionChoice correctOption,
        Integer marksAwarded,
        Integer maxMarks
) {
}
