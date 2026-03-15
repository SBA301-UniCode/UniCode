package com.example.unicode.mapper;

import com.example.unicode.dto.response.CertificateResponse;
import com.example.unicode.entity.Certificate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CertificateMapper {

    @Mapping(source = "learner.userId", target = "learnerId")
    @Mapping(source = "learner.name", target = "learnerName")
    @Mapping(source = "learner.email", target = "learnerEmail")
    @Mapping(source = "course.courseId", target = "courseId")
    @Mapping(source = "course.title", target = "courseTitle")
    @Mapping(source = "course.instructors.name", target = "instructorName")
    CertificateResponse toResponse(Certificate certificate);

    List<CertificateResponse> toResponseList(List<Certificate> certificates);
}
