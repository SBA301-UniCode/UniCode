package com.example.unicode.service;

import com.example.unicode.dto.request.ChapterCreateRequest;
import com.example.unicode.dto.request.ChapterUpdateRequest;
import com.example.unicode.dto.response.ChapterResponse;
import com.example.unicode.dto.response.PageResponse;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for Chapter management operations.
 * Follows Interface Segregation Principle (ISP).
 */
public interface ChapterService {

    /**
     * Create a new chapter for a course
     * @param request chapter creation request
     * @return created chapter response
     */
    ChapterResponse create(ChapterCreateRequest request);

    /**
     * Get chapter by ID
     * @param chapterId chapter UUID
     * @return chapter response
     */
    ChapterResponse getById(UUID chapterId);

    /**
     * Get all active chapters with pagination
     * @param page page number
     * @param size page size
     * @return paginated chapter responses
     */
    PageResponse<ChapterResponse> getAll(int page, int size);

    /**
     * Get all chapters for a specific course (ordered by orderIndex)
     * @param courseId course UUID
     * @return list of chapter responses
     */
    List<ChapterResponse> getByCourseId(UUID courseId);

    /**
     * Update chapter by ID
     * @param chapterId chapter UUID
     * @param request update request
     * @return updated chapter response
     */
    ChapterResponse update(UUID chapterId, ChapterUpdateRequest request);

    /**
     * Soft delete chapter by ID
     * @param chapterId chapter UUID
     */
    void delete(UUID chapterId);
}
