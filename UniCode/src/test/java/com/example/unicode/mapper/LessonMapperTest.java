package com.example.unicode.mapper;

import com.example.unicode.dto.request.LessonCreateRequest;
import com.example.unicode.dto.request.LessonUpdateRequest;
import com.example.unicode.entity.Chapter;
import com.example.unicode.entity.Content;
import com.example.unicode.entity.Lesson;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LessonMapperTest {

    private final LessonMapper mapper = Mappers.getMapper(LessonMapper.class);

    @Test
    void toEntityAndUpdateShouldMapFields() {
        LessonCreateRequest create = new LessonCreateRequest(UUID.randomUUID(), "L1", 1);
        Lesson entity = mapper.toEntity(create);

        assertEquals("L1", entity.getTitle());
        assertEquals(1, entity.getOrderIndex());
        assertNull(entity.getChapter());

        entity.setTitle("Old");
        entity.setOrderIndex(2);
        mapper.updateEntity(new LessonUpdateRequest(null, 10), entity);
        assertEquals("Old", entity.getTitle());
        assertEquals(10, entity.getOrderIndex());
    }

    @Test
    void toResponseShouldMapNestedChapterAndContentCount() {
        Chapter chapter = new Chapter();
        chapter.setChapterId(UUID.randomUUID());
        chapter.setTitle("Chapter A");

        Lesson lesson = new Lesson();
        lesson.setLessonId(UUID.randomUUID());
        lesson.setTitle("Lesson A");
        lesson.setOrderIndex(4);
        lesson.setChapter(chapter);
        lesson.setContentList(List.of(new Content(), new Content(), new Content()));

        var response = mapper.toResponse(lesson);
        assertEquals(chapter.getChapterId(), response.getChapterId());
        assertEquals("Chapter A", response.getChapterTitle());
        assertEquals(3, response.getContentCount());

        assertEquals(1, mapper.toResponseList(List.of(lesson)).size());
        assertNull(mapper.toResponseList(null));
    }
}

