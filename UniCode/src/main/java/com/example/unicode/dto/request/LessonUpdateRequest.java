package com.example.unicode.dto.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonUpdateRequest {

    private String title;

    @Min(value = 0, message = "Order index must be zero or positive")
    private Integer orderIndex;
}
