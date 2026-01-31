package com.example.unicode.service.impl;

import com.example.unicode.dto.request.LessonCreateRequest;
import com.example.unicode.dto.request.LessonUpdateRequest;
import com.example.unicode.dto.response.LessonResponse;
import com.example.unicode.dto.response.PageResponse;
import com.example.unicode.entity.Chapter;
import com.example.unicode.entity.Lesson;
import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;
import com.example.unicode.mapper.LessonMapper;
import com.example.unicode.repository.ChapterRepo;
import com.example.unicode.repository.LessonRepo;
import com.example.unicode.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of LessonService.
 * Follows Single Responsibility Principle (SRP) - handles only lesson business logic.
 * Uses Dependency Injection for loose coupling (DIP).
 */
@Service
@RequiredArgsConstructor
@Transactional
public class LessonServiceImpl implements LessonService {

    private final LessonRepo lessonRepo;
    private final ChapterRepo chapterRepo;
    private final LessonMapper lessonMapper;

    @Override
    public LessonResponse create(LessonCreateRequest request) {
        // Validate chapter exists
        Chapter chapter = chapterRepo.findByChapterIdAndDeletedFalse(request.getChapterId())
                .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));

        // Map request to entity
        Lesson lesson = lessonMapper.toEntity(request);
        lesson.setChapter(chapter);

        lesson = lessonRepo.save(lesson);
        return lessonMapper.toResponse(lesson);
    }

    @Override
    @Transactional(readOnly = true)
    public LessonResponse getById(UUID lessonId) {
        Lesson lesson = findLessonOrThrow(lessonId);
        return lessonMapper.toResponse(lesson);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<LessonResponse> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Lesson> lessonPage = lessonRepo.findAllByDeletedFalse(pageable);

        return PageResponse.<LessonResponse>builder()
                .content(lessonMapper.toResponseList(lessonPage.getContent()))
                .currentPage(lessonPage.getNumber())
                .pageSize(lessonPage.getSize())
                .totalElements(lessonPage.getTotalElements())
                .totalPages(lessonPage.getTotalPages())
                .first(lessonPage.isFirst())
                .last(lessonPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonResponse> getByChapterId(UUID chapterId) {
        // Validate chapter exists
        if (!chapterRepo.findByChapterIdAndDeletedFalse(chapterId).isPresent()) {
            throw new AppException(ErrorCode.CHAPTER_NOT_FOUND);
        }

        return lessonMapper.toResponseList(
                lessonRepo.findByChapter_ChapterIdAndDeletedFalseOrderByOrderIndexAsc(chapterId));
    }

    @Override
    public LessonResponse update(UUID lessonId, LessonUpdateRequest request) {
        Lesson lesson = findLessonOrThrow(lessonId);

        // Use MapStruct for partial update (null values are ignored)
        lessonMapper.updateEntity(request, lesson);

        lesson = lessonRepo.save(lesson);
        return lessonMapper.toResponse(lesson);
    }

    @Override
    public void delete(UUID lessonId) {
        Lesson lesson = findLessonOrThrow(lessonId);

        // Soft delete
        lesson.setDeleted(true);
        lesson.setDeletedAt(LocalDateTime.now());
        lesson.setDeletedBy(getCurrentUser());

        lessonRepo.save(lesson);
    }

    /**
     * DRY: Extracts common lesson lookup logic
     */
    private Lesson findLessonOrThrow(UUID lessonId) {
        return lessonRepo.findByLessonIdAndDeletedFalse(lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));
    }

    /**
     * Get current authenticated user for audit fields
     */
    private String getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "SYSTEM";
        }
        return authentication.getName();
    }
}
