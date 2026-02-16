package com.example.unicode.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SylabusResponse {

    private String sylabusId;

    private String courseContent;

    private String method;

    private String referenceMaterial;

    private UUID courseId;

    private String courseTitle;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
