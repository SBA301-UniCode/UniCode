package com.example.unicode.service.impl;

import com.example.unicode.dto.request.ChapterCreateRequest;
import com.example.unicode.dto.request.ChapterUpdateRequest;
import com.example.unicode.dto.response.ChapterResponse;
import com.example.unicode.dto.response.PageResponse;
import com.example.unicode.entity.Chapter;
import com.example.unicode.entity.Course;
import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;
import com.example.unicode.mapper.ChapterMapper;
import com.example.unicode.repository.ChapterRepository;
import com.example.unicode.repository.CourseRepository;
import com.example.unicode.service.ChapterService;
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
 * Implementation of ChapterService.
 * Follows Single Responsibility Principle (SRP) - handles only chapter business logic.
 * Uses Dependency Injection for loose coupling (DIP).
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ChapterServiceImpl implements ChapterService {

    private final ChapterRepository chapterRepository;
    private final CourseRepository courseRepository;
    private final ChapterMapper chapterMapper;

    @Override
    public ChapterResponse create(ChapterCreateRequest request) {
        // Validate course exists
        Course course = courseRepository.findByCourseIdAndDeletedFalse(request.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        // Map request to entity
        Chapter chapter = chapterMapper.toEntity(request);
        chapter.setCourse(course);

        chapter = chapterRepository.save(chapter);
        return chapterMapper.toResponse(chapter);
    }

    @Override
    @Transactional(readOnly = true)
    public ChapterResponse getById(UUID chapterId) {
        Chapter chapter = findChapterOrThrow(chapterId);
        return chapterMapper.toResponse(chapter);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ChapterResponse> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Chapter> chapterPage = chapterRepository.findAllByDeletedFalse(pageable);

        return PageResponse.<ChapterResponse>builder()
                .content(chapterMapper.toResponseList(chapterPage.getContent()))
                .currentPage(chapterPage.getNumber())
                .pageSize(chapterPage.getSize())
                .totalElements(chapterPage.getTotalElements())
                .totalPages(chapterPage.getTotalPages())
                .first(chapterPage.isFirst())
                .last(chapterPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChapterResponse> getByCourseId(UUID courseId) {
        // Validate course exists
        if (!courseRepository.findByCourseIdAndDeletedFalse(courseId).isPresent()) {
            throw new AppException(ErrorCode.COURSE_NOT_FOUND);
        }

        return chapterMapper.toResponseList(
                chapterRepository.findByCourse_CourseIdAndDeletedFalseOrderByOrderIndexAsc(courseId));
    }

    @Override
    public ChapterResponse update(UUID chapterId, ChapterUpdateRequest request) {
        Chapter chapter = findChapterOrThrow(chapterId);

        // Use MapStruct for partial update (null values are ignored)
        chapterMapper.updateEntity(request, chapter);

        chapter = chapterRepository.save(chapter);
        return chapterMapper.toResponse(chapter);
    }

    @Override
    public void delete(UUID chapterId) {
        Chapter chapter = findChapterOrThrow(chapterId);

        // Soft delete
        chapter.setDeleted(true);
        chapter.setDeletedAt(LocalDateTime.now());
        chapter.setDeletedBy(getCurrentUser());

        chapterRepository.save(chapter);
    }

    /**
     * DRY: Extracts common chapter lookup logic
     */
    private Chapter findChapterOrThrow(UUID chapterId) {
        return chapterRepository.findByChapterIdAndDeletedFalse(chapterId)
                .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));
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
