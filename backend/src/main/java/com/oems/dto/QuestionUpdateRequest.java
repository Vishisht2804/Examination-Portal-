package com.oems.dto;

import com.oems.model.OptionChoice;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record QuestionUpdateRequest(
        @NotBlank String questionText,
        @NotBlank String optionA,
        @NotBlank String optionB,
        @NotBlank String optionC,
        @NotBlank String optionD,
        @NotNull OptionChoice correctOption,
        @NotNull @Min(1) Integer marks
) {
}
