package com.example.unicode.service.impl;

import com.example.unicode.dto.request.ContentCreateRequest;
import com.example.unicode.dto.request.ContentUpdateRequest;
import com.example.unicode.dto.response.ContentResponse;
import com.example.unicode.entity.Content;
import com.example.unicode.entity.Lesson;
import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;
import com.example.unicode.mapper.ContentMapper;
import com.example.unicode.repository.ContentRepo;
import com.example.unicode.repository.LessonRepository;
import com.example.unicode.service.ContentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ContentServiceImpl implements ContentService {
    private final ContentRepo contentRepo;
    private final ContentMapper contentMapper;
    private final LessonRepository lessonRepo;

    @Override
    public ContentResponse create(ContentCreateRequest request) {
        Content content = contentMapper.toEntity(request);

        return contentMapper.toResponse(contentRepo.save(content));
    }

    @Override
    public List<ContentResponse> getAllContentAndLesson(UUID lesonId) {
        Lesson lesson = lessonRepo.getReferenceById(lesonId);
        List<Content> contents = contentRepo.findAllByLesson(lesson);
        return contentMapper.toResponseList(contents);
    }

    @Override
    public void delete(UUID contentId) {
        Content content = contentRepo.getReferenceById(contentId);

        content.setDeleted(true);
        content.setDeletedAt(LocalDateTime.now());
        content.setDeletedBy(getCurrentUser());

        contentRepo.save(content);
    }

    @Override
    public ContentResponse update(UUID contentId, ContentUpdateRequest request) {
        Content content = contentRepo.findByContentIdAndDeletedFalse(contentId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTENT_NOT_FOUND));

        content.setContentType(request.getContentType());

        if (request.getLessonId() != null) {
            Lesson newLesson = lessonRepo.findById(request.getLessonId())
                    .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));
            content.setLesson(newLesson);
        }
        content.setUpdatedAt(LocalDateTime.now());
        content.setUpdatedBy(getCurrentUser());

        return contentMapper.toResponse(contentRepo.save(content));
    }




    private String getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "SYSTEM";
        }
        return authentication.getName();
    }

}



