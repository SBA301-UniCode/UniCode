package com.example.unicode.dto.request;

import com.example.unicode.entity.Lesson;
import com.example.unicode.enums.ContentType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentCreateRequest {


    @NotNull(message = "ContentType is required")
    private ContentType contentType;
    @NotNull(message = "LessonId is required")
    private UUID lessonId;



}
