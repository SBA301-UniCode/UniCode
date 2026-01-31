package com.example.unicode.service;

import com.example.unicode.dto.request.CourseCreateRequest;
import com.example.unicode.dto.request.CourseUpdateRequest;
import com.example.unicode.dto.response.CourseResponse;
import com.example.unicode.dto.response.PageResponse;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for Course management operations.
 * Follows Interface Segregation Principle (ISP) - defines only course-specific operations.
 */
public interface CourseService {

    /**
     * Create a new course
     * @param request course creation request
     * @return created course response
     */
    CourseResponse create(CourseCreateRequest request);

    /**
     * Get course by ID
     * @param courseId course UUID
     * @return course response
     */
    CourseResponse getById(UUID courseId);

    /**
     * Get all active courses (not deleted) with pagination
     * @param page page number (0-indexed)
     * @param size page size (default 10)
     * @return paginated course responses
     */
    PageResponse<CourseResponse> getAll(int page, int size);

    /**
     * Update course by ID
     * @param courseId course UUID
     * @param request update request
     * @return updated course response
     */
    CourseResponse update(UUID courseId, CourseUpdateRequest request);

    /**
     * Soft delete course by ID
     * @param courseId course UUID
     */
    void delete(UUID courseId);
}
