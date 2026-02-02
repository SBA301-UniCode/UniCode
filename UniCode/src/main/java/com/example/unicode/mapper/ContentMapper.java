package com.example.unicode.mapper;


import com.example.unicode.dto.request.ContentCreateRequest;
import com.example.unicode.dto.response.CertificateResponse;
import com.example.unicode.dto.response.ContentResponse;
import com.example.unicode.entity.Certificate;
import com.example.unicode.entity.Content;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ContentMapper {
    @Mapping(source = "lessonId", target = "lesson.lessonId")
    Content toEntity(ContentCreateRequest request);
    @Mapping(source = "lesson.lessonId", target = "lessonId")
    ContentResponse toResponse(Content content); // tự lo việc tạo object Content và setContentType, setLessonId
    List<ContentResponse> toResponseList(List<Content> contents);

}