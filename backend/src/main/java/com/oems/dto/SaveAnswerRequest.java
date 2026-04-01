package com.oems.dto;

import com.oems.model.OptionChoice;
import jakarta.validation.constraints.NotNull;

public record SaveAnswerRequest(
        @NotNull String questionId,
        OptionChoice selectedOption
) {
}
