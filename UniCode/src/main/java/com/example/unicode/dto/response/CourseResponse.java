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
public class CourseResponse {

    private UUID courseId;

    private String title;

    private String description;

    private Double price;

    private String instructorId;

    private String instructorName;

    private String sylabusId;

    private Integer chapterCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
