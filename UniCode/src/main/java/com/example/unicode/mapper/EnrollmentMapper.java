package com.example.unicode.mapper;

import com.example.unicode.dto.response.EnrolmentResponse;
import com.example.unicode.entity.Enrollment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = {CourseMapper.class})
public interface EnrollmentMapper {
    @Mapping(target = "courseResponse",source = "course")
    EnrolmentResponse entityToResponse(Enrollment enrollment);
}
