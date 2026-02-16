package com.example.unicode.service;

import com.example.unicode.dto.request.LessonCreateRequest;
import com.example.unicode.dto.request.LessonUpdateRequest;
import com.example.unicode.dto.response.LessonResponse;
import com.example.unicode.dto.response.PageResponse;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for Lesson management operations.
 * Follows Interface Segregation Principle (ISP).
 */
public interface LessonService {

    /**
     * Create a new lesson for a chapter
     * @param request lesson creation request
     * @return created lesson response
     */
    LessonResponse create(LessonCreateRequest request);

    /**
     * Get lesson by ID
     * @param lessonId lesson UUID
     * @return lesson response
     */
    LessonResponse getById(UUID lessonId);

    /**
     * Get all active lessons with pagination
     * @param page page number
     * @param size page size
     * @return paginated lesson responses
     */
    PageResponse<LessonResponse> getAll(int page, int size);

    /**
     * Get all lessons for a specific chapter (ordered by orderIndex)
     * @param chapterId chapter UUID
     * @return list of lesson responses
     */
    List<LessonResponse> getByChapterId(UUID chapterId);

    /**
     * Update lesson by ID
     * @param lessonId lesson UUID
     * @param request update request
     * @return updated lesson response
     */
    LessonResponse update(UUID lessonId, LessonUpdateRequest request);

    /**
     * Soft delete lesson by ID
     * @param lessonId lesson UUID
     */
    void delete(UUID lessonId);
}
