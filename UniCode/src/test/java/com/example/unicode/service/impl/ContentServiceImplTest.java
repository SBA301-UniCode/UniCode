package com.example.unicode.service.impl;

import com.example.unicode.dto.request.ContentUpdateRequest;
import com.example.unicode.entity.Content;
import com.example.unicode.enums.ContentType;
import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;
import com.example.unicode.mapper.ContentMapper;
import com.example.unicode.repository.ContentRepo;
import com.example.unicode.repository.LessonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContentServiceImplTest {

    @Mock
    private ContentRepo contentRepo;
    @Mock
    private ContentMapper contentMapper;
    @Mock
    private LessonRepository lessonRepo;

    @InjectMocks
    private ContentServiceImpl contentService;

    @Test
    void updateShouldThrowWhenContentNotFound() {
        UUID contentId = UUID.randomUUID();
        ContentUpdateRequest request = new ContentUpdateRequest(ContentType.VIDEO, UUID.randomUUID());
        when(contentRepo.findByContentIdAndDeletedFalse(contentId)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> contentService.update(contentId, request));

        assertEquals(ErrorCode.CONTENT_NOT_FOUND, ex.getErrorCode());
    }
}

