package com.oems.dto;

import com.oems.model.OptionChoice;

public record AnswerStateResponse(
        String questionId,
        OptionChoice selectedOption
) {
}
