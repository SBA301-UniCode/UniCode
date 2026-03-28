package com.example.unicode.service;

import com.example.unicode.dto.request.CourseCreateRequest;
import com.example.unicode.dto.request.CourseUpdateRequest;
import com.example.unicode.dto.request.ReportRequest;
import com.example.unicode.dto.response.CourseResponse;
import com.example.unicode.dto.response.InstructorReport;
import com.example.unicode.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

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
    CourseResponse create(CourseCreateRequest request, MultipartFile file);

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


    InstructorReport instructorReport(ReportRequest request);

    Page<CourseResponse>  getMyCoures(String keysearch,String sortBy,String direction,boolean deleted, int page, int size);

    void active(UUID courseId);
}
