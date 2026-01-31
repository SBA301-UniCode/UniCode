package com.example.unicode.service.impl;

import com.example.unicode.dto.request.CourseCreateRequest;
import com.example.unicode.dto.request.CourseUpdateRequest;
import com.example.unicode.dto.response.CourseResponse;
import com.example.unicode.dto.response.PageResponse;
import com.example.unicode.entity.Course;
import com.example.unicode.entity.Users;
import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;
import com.example.unicode.mapper.CourseMapper;
import com.example.unicode.repository.CourseRepo;
import com.example.unicode.repository.UsersRepo;
import com.example.unicode.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implementation of CourseService.
 * Follows Single Responsibility Principle (SRP) - handles only course business logic.
 * Uses Dependency Injection for loose coupling (DIP).
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepo courseRepo;
    private final UsersRepo usersRepo;
    private final CourseMapper courseMapper;

    @Override
    public CourseResponse create(CourseCreateRequest request) {
        // Validate instructor exists
        Users instructor = usersRepo.findByUserIdAndDeletedFalse(request.getInstructorId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Map request to entity
        Course course = courseMapper.toEntity(request);
        course.setInstructors(instructor);

        course = courseRepo.save(course);
        return courseMapper.toResponse(course);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse getById(UUID courseId) {
        Course course = findCourseOrThrow(courseId);
        return courseMapper.toResponse(course);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CourseResponse> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Course> coursePage = courseRepo.findAllByDeletedFalse(pageable);

        return PageResponse.<CourseResponse>builder()
                .content(courseMapper.toResponseList(coursePage.getContent()))
                .currentPage(coursePage.getNumber())
                .pageSize(coursePage.getSize())
                .totalElements(coursePage.getTotalElements())
                .totalPages(coursePage.getTotalPages())
                .first(coursePage.isFirst())
                .last(coursePage.isLast())
                .build();
    }

    @Override
    public CourseResponse update(UUID courseId, CourseUpdateRequest request) {
        Course course = findCourseOrThrow(courseId);

        // Use MapStruct for partial update (null values are ignored)
        courseMapper.updateEntity(request, course);

        course = courseRepo.save(course);
        return courseMapper.toResponse(course);
    }

    @Override
    public void delete(UUID courseId) {
        Course course = findCourseOrThrow(courseId);

        // Soft delete
        course.setDeleted(true);
        course.setDeletedAt(LocalDateTime.now());
        course.setDeletedBy(getCurrentUser());

        courseRepo.save(course);
    }

    /**
     * DRY: Extracts common course lookup logic
     */
    private Course findCourseOrThrow(UUID courseId) {
        return courseRepo.findByCourseIdAndDeletedFalse(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
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
