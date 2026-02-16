package com.example.unicode.dto.request;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseUpdateRequest {

    private String title;

    private String description;

    @PositiveOrZero(message = "Price must be zero or positive")
    private Double price;
}
