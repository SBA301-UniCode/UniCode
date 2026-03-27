package com.example.unicode.mapper;

import com.example.unicode.entity.Certificate;
import com.example.unicode.entity.Course;
import com.example.unicode.entity.Users;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CertificateMapperTest {

    private final CertificateMapper mapper = Mappers.getMapper(CertificateMapper.class);

    @Test
    void toResponseShouldFlattenNestedFields() {
        Users learner = new Users();
        learner.setUserId(UUID.randomUUID());
        learner.setName("Learner");
        learner.setEmail("learner@test.com");

        Users instructor = new Users();
        instructor.setName("Instructor A");

        Course course = new Course();
        course.setCourseId(UUID.randomUUID());
        course.setTitle("Java");
        course.setInstructors(instructor);

        Certificate cert = new Certificate();
        cert.setCertificateId(UUID.randomUUID());
        cert.setLearner(learner);
        cert.setCourse(course);
        cert.setKeyUrl("https://cert");

        var response = mapper.toResponse(cert);
        assertEquals(learner.getUserId(), response.getLearnerId());
        assertEquals("Learner", response.getLearnerName());
        assertEquals("learner@test.com", response.getLearnerEmail());
        assertEquals(course.getCourseId(), response.getCourseId());
        assertEquals("Instructor A", response.getInstructorName());
        assertEquals("https://cert", response.getCerticateUrl());

        assertEquals(1, mapper.toResponseList(List.of(cert)).size());
        assertNull(mapper.toResponseList(null));
    }
}

