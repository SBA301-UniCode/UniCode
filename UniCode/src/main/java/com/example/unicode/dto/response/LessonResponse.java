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
public class LessonResponse {

    private UUID lessonId;

    private UUID chapterId;

    private String chapterTitle;

    private String title;

    private Integer orderIndex;

    private Integer contentCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
