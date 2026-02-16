package com.example.unicode.mapper;

import com.example.unicode.dto.request.ChapterCreateRequest;
import com.example.unicode.dto.request.ChapterUpdateRequest;
import com.example.unicode.dto.response.ChapterResponse;
import com.example.unicode.entity.Chapter;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChapterMapper {

    @Mapping(target = "chapterId", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "lessonList", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    Chapter toEntity(ChapterCreateRequest request);

    @Mapping(target = "courseId", source = "course.courseId")
    @Mapping(target = "courseTitle", source = "course.title")
    @Mapping(target = "lessonCount", expression = "java(chapter.getLessonList() != null ? chapter.getLessonList().size() : 0)")
    ChapterResponse toResponse(Chapter chapter);

    List<ChapterResponse> toResponseList(List<Chapter> chapters);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "chapterId", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "lessonList", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    void updateEntity(ChapterUpdateRequest request, @MappingTarget Chapter chapter);
}
