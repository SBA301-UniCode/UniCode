package com.example.unicode.service.impl;

import com.example.unicode.dto.request.LessonCreateRequest;
import com.example.unicode.entity.Lesson;
import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;
import com.example.unicode.mapper.LessonMapper;
import com.example.unicode.repository.ChapterRepository;
import com.example.unicode.repository.LessonRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LessonServiceImplTest {

    @Mock
    private LessonRepository lessonRepository;
    @Mock
    private ChapterRepository chapterRepository;
    @Mock
    private LessonMapper lessonMapper;

    @InjectMocks
    private LessonServiceImpl lessonService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createShouldThrowWhenChapterNotFound() {
        LessonCreateRequest request = new LessonCreateRequest(UUID.randomUUID(), "L1", 1);
        when(chapterRepository.findByChapterIdAndDeletedFalse(request.getChapterId())).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> lessonService.create(request));

        assertEquals(ErrorCode.CHAPTER_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void deleteShouldSoftDeleteLesson() {
        UUID lessonId = UUID.randomUUID();
        Lesson lesson = new Lesson();
        when(lessonRepository.findByLessonIdAndDeletedFalse(lessonId)).thenReturn(Optional.of(lesson));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin@test.com", "pwd", AuthorityUtils.createAuthorityList("ROLE_ADMIN"))
        );

        lessonService.delete(lessonId);

        ArgumentCaptor<Lesson> captor = ArgumentCaptor.forClass(Lesson.class);
        verify(lessonRepository).save(captor.capture());
        assertTrue(captor.getValue().getDeleted());
        assertEquals("admin@test.com", captor.getValue().getDeletedBy());
    }
}

