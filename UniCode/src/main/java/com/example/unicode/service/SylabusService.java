package com.example.unicode.service;

import com.example.unicode.dto.request.SylabusCreateRequest;
import com.example.unicode.dto.request.SylabusUpdateRequest;
import com.example.unicode.dto.response.SylabusResponse;
import com.example.unicode.dto.response.PageResponse;

import java.util.List;

/**
 * Service interface for Sylabus management operations.
 * Follows Interface Segregation Principle (ISP).
 */
public interface SylabusService {

    /**
     * Create a new syllabus
     * @param request syllabus creation request
     * @return created syllabus response
     */
    SylabusResponse create(SylabusCreateRequest request);

    /**
     * Get syllabus by ID
     * @param sylabusId syllabus ID
     * @return syllabus response
     */
    SylabusResponse getById(String sylabusId);

    /**
     * Get all active syllabuses with pagination
     * @param page page number
     * @param size page size
     * @return paginated syllabus responses
     */
    PageResponse<SylabusResponse> getAll(int page, int size);

    /**
     * Update syllabus by ID
     * @param sylabusId syllabus ID
     * @param request update request
     * @return updated syllabus response
     */
    SylabusResponse update(String sylabusId, SylabusUpdateRequest request);

    /**
     * Soft delete syllabus by ID
     * @param sylabusId syllabus ID
     */
    void delete(String sylabusId);
}
