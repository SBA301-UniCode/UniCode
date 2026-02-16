package com.example.unicode.mapper;

import com.example.unicode.dto.request.LessonCreateRequest;
import com.example.unicode.dto.request.LessonUpdateRequest;
import com.example.unicode.dto.response.LessonResponse;
import com.example.unicode.entity.Lesson;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LessonMapper {

    @Mapping(target = "lessonId", ignore = true)
    @Mapping(target = "chapter", ignore = true)
    @Mapping(target = "contentList", ignore = true)
    @Mapping(target = "questionBank", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    Lesson toEntity(LessonCreateRequest request);

    @Mapping(target = "chapterId", source = "chapter.chapterId")
    @Mapping(target = "chapterTitle", source = "chapter.title")
    @Mapping(target = "contentCount", expression = "java(lesson.getContentList() != null ? lesson.getContentList().size() : 0)")
    LessonResponse toResponse(Lesson lesson);

    List<LessonResponse> toResponseList(List<Lesson> lessons);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "lessonId", ignore = true)
    @Mapping(target = "chapter", ignore = true)
    @Mapping(target = "contentList", ignore = true)
    @Mapping(target = "questionBank", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    void updateEntity(LessonUpdateRequest request, @MappingTarget Lesson lesson);
}
