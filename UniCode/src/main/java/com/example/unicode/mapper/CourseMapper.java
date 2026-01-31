package com.example.unicode.mapper;

import com.example.unicode.dto.request.CourseCreateRequest;
import com.example.unicode.dto.request.CourseUpdateRequest;
import com.example.unicode.dto.response.CourseResponse;
import com.example.unicode.entity.Course;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    @Mapping(target = "courseId", ignore = true)
    @Mapping(target = "instructors", ignore = true)
    @Mapping(target = "sylabus", ignore = true)
    @Mapping(target = "rateConditions", ignore = true)
    @Mapping(target = "feedbacks", ignore = true)
    @Mapping(target = "certificate", ignore = true)
    @Mapping(target = "subcriptionList", ignore = true)
    @Mapping(target = "enrollmentList", ignore = true)
    @Mapping(target = "chapterList", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    Course toEntity(CourseCreateRequest request);

    @Mapping(target = "instructorId", source = "instructors.userId", qualifiedByName = "uuidToString")
    @Mapping(target = "instructorName", source = "instructors.name")
    @Mapping(target = "sylabusId", source = "sylabus.sylabusId")
    @Mapping(target = "chapterCount", expression = "java(course.getChapterList() != null ? course.getChapterList().size() : 0)")
    CourseResponse toResponse(Course course);

    List<CourseResponse> toResponseList(List<Course> courses);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "courseId", ignore = true)
    @Mapping(target = "instructors", ignore = true)
    @Mapping(target = "sylabus", ignore = true)
    @Mapping(target = "rateConditions", ignore = true)
    @Mapping(target = "feedbacks", ignore = true)
    @Mapping(target = "certificate", ignore = true)
    @Mapping(target = "subcriptionList", ignore = true)
    @Mapping(target = "enrollmentList", ignore = true)
    @Mapping(target = "chapterList", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    void updateEntity(CourseUpdateRequest request, @MappingTarget Course course);

    @Named("uuidToString")
    default String uuidToString(java.util.UUID uuid) {
        return uuid != null ? uuid.toString() : null;
    }
}
