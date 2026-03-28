package com.example.unicode.mapper;

import com.example.unicode.dto.request.CourseCreateRequest;
import com.example.unicode.dto.request.CourseUpdateRequest;
import com.example.unicode.entity.Chapter;
import com.example.unicode.entity.Course;
import com.example.unicode.entity.Sylabus;
import com.example.unicode.entity.Users;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CourseMapperTest {

    private final CourseMapper mapper = Mappers.getMapper(CourseMapper.class);

    @Test
    void toEntityAndUpdateEntityShouldMapExpectedFields() {
        CourseCreateRequest create = new CourseCreateRequest("Java", "desc", 99.0, UUID.randomUUID(), "S1");

        Course entity = mapper.toEntity(create);
        assertEquals("Java", entity.getTitle());
        assertEquals("desc", entity.getDescription());
        assertEquals(99.0, entity.getPrice());
        assertNull(entity.getCourseId());
        assertNull(entity.getInstructors());

        entity.setTitle("Old");
        entity.setDescription("OldDesc");
        entity.setPrice(10.0);
        mapper.updateEntity(new CourseUpdateRequest(null, null, 20.0), entity);
        assertEquals("Old", entity.getTitle());
        assertEquals("OldDesc", entity.getDescription());
        assertEquals(20.0, entity.getPrice());
    }

    @Test
    void toResponseAndHelpersShouldMapNestedAndCounts() {
        UUID instructorId = UUID.randomUUID();
        Users instructor = new Users();
        instructor.setUserId(instructorId);
        instructor.setName("Teacher");

        Sylabus sylabus = new Sylabus();
        sylabus.setSylabusId("SYL-1");

        Course course = new Course();
        course.setCourseId(UUID.randomUUID());
        course.setTitle("Java");
        course.setDescription("desc");
        course.setPrice(50.0);
        course.setInstructors(instructor);
        course.setSylabus(sylabus);
        course.setChapterList(List.of(new Chapter(), new Chapter()));

        var response = mapper.toResponse(course);
        assertEquals(instructorId.toString(), response.getInstructorId());
        assertEquals("Teacher", response.getInstructorName());
        assertEquals("SYL-1", response.getSylabusId());
        assertEquals(2, response.getChapterCount());

        assertEquals(1, mapper.toResponseList(List.of(course)).size());
        assertNull(mapper.toResponseList(null));
        assertNull(mapper.uuidToString(null));
        assertEquals(instructorId.toString(), mapper.uuidToString(instructorId));
    }
}

