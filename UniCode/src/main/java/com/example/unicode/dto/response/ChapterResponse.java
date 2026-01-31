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
public class ChapterResponse {

    private UUID chapterId;

    private UUID courseId;

    private String courseTitle;

    private String title;

    private Integer orderIndex;

    private Integer lessonCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
