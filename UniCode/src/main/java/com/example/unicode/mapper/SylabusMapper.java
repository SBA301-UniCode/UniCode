package com.example.unicode.mapper;

import com.example.unicode.dto.request.SylabusCreateRequest;
import com.example.unicode.dto.request.SylabusUpdateRequest;
import com.example.unicode.dto.response.SylabusResponse;
import com.example.unicode.entity.Sylabus;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SylabusMapper {

    @Mapping(target = "sylabusId", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    Sylabus toEntity(SylabusCreateRequest request);

    @Mapping(target = "courseId", source = "course.courseId")
    @Mapping(target = "courseTitle", source = "course.title")
    SylabusResponse toResponse(Sylabus sylabus);

    List<SylabusResponse> toResponseList(List<Sylabus> sylabuses);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "sylabusId", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    void updateEntity(SylabusUpdateRequest request, @MappingTarget Sylabus sylabus);
}
