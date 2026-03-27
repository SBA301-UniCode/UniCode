package com.example.unicode.service.impl;

import com.example.unicode.dto.request.ChapterCreateRequest;
import com.example.unicode.entity.Chapter;
import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;
import com.example.unicode.mapper.ChapterMapper;
import com.example.unicode.repository.ChapterRepository;
import com.example.unicode.repository.CourseRepository;
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
class ChapterServiceImplTest {

    @Mock
    private ChapterRepository chapterRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private ChapterMapper chapterMapper;

    @InjectMocks
    private ChapterServiceImpl chapterService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createShouldThrowWhenCourseNotFound() {
        ChapterCreateRequest request = new ChapterCreateRequest(UUID.randomUUID(), "Ch1", 1);
        when(courseRepository.findByCourseIdAndDeletedFalse(request.getCourseId())).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> chapterService.create(request));

        assertEquals(ErrorCode.COURSE_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void deleteShouldSoftDeleteChapter() {
        UUID chapterId = UUID.randomUUID();
        Chapter chapter = new Chapter();
        when(chapterRepository.findByChapterIdAndDeletedFalse(chapterId)).thenReturn(Optional.of(chapter));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin@test.com", "pwd", AuthorityUtils.createAuthorityList("ROLE_ADMIN"))
        );

        chapterService.delete(chapterId);

        ArgumentCaptor<Chapter> captor = ArgumentCaptor.forClass(Chapter.class);
        verify(chapterRepository).save(captor.capture());
        assertTrue(captor.getValue().getDeleted());
        assertEquals("admin@test.com", captor.getValue().getDeletedBy());
    }
}

