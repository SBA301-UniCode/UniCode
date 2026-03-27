package com.example.unicode.mapper;

import com.example.unicode.dto.request.ChapterCreateRequest;
import com.example.unicode.dto.request.ChapterUpdateRequest;
import com.example.unicode.entity.Chapter;
import com.example.unicode.entity.Course;
import com.example.unicode.entity.Lesson;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ChapterMapperTest {

    private final ChapterMapper mapper = Mappers.getMapper(ChapterMapper.class);

    @Test
    void toEntityAndUpdateShouldMapFields() {
        ChapterCreateRequest create = new ChapterCreateRequest(UUID.randomUUID(), "Ch1", 1);
        Chapter entity = mapper.toEntity(create);

        assertEquals("Ch1", entity.getTitle());
        assertEquals(1, entity.getOrderIndex());
        assertNull(entity.getCourse());

        entity.setTitle("Old");
        entity.setOrderIndex(3);
        mapper.updateEntity(new ChapterUpdateRequest(null, 7), entity);
        assertEquals("Old", entity.getTitle());
        assertEquals(7, entity.getOrderIndex());
    }

    @Test
    void toResponseShouldMapNestedCourseAndLessonCount() {
        Course course = new Course();
        course.setCourseId(UUID.randomUUID());
        course.setTitle("Java");

        Chapter chapter = new Chapter();
        chapter.setChapterId(UUID.randomUUID());
        chapter.setTitle("Ch1");
        chapter.setOrderIndex(2);
        chapter.setCourse(course);
        chapter.setLessonList(List.of(new Lesson(), new Lesson()));

        var response = mapper.toResponse(chapter);
        assertEquals(course.getCourseId(), response.getCourseId());
        assertEquals("Java", response.getCourseTitle());
        assertEquals(2, response.getLessonCount());

        assertEquals(1, mapper.toResponseList(List.of(chapter)).size());
        assertNull(mapper.toResponseList(null));
    }
}

