package com.example.unicode.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SylabusCreateRequest {

    private UUID courseId;
    @NotBlank(message = "Course content is required")
    private String courseContent;

    private String method;

    private String referenceMaterial;
}
