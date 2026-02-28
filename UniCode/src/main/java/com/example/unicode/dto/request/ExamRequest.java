package com.example.unicode.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamRequest {
    @NotBlank(message = "Name is required")
    private String name;
    @NotNull(message = "Duration is required")
    @Positive( message = "Duration must be greater than zero")
    private int duration;
    @NotNull(message = "Pass score is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Pass score must be zero or positive")
    @DecimalMax(value = "100.0", inclusive = true, message = "Pass score must be less than or equal to 100")
    private double passScore;
    @NotNull(message = "Number of question is required")
    private int numberQuestions;

}
