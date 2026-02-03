package com.example.unicode.service.impl;

import com.example.unicode.dto.request.SylabusCreateRequest;
import com.example.unicode.dto.request.SylabusUpdateRequest;
import com.example.unicode.dto.response.SylabusResponse;
import com.example.unicode.dto.response.PageResponse;
import com.example.unicode.entity.Course;
import com.example.unicode.entity.Sylabus;
import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;
import com.example.unicode.mapper.SylabusMapper;
import com.example.unicode.repository.CourseRepository;
import com.example.unicode.repository.SylabusRepository;
import com.example.unicode.service.SylabusService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Implementation of SylabusService.
 * Follows Single Responsibility Principle (SRP) - handles only syllabus business logic.
 * Uses Dependency Injection for loose coupling (DIP).
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SylabusServiceImpl implements SylabusService {

    private final SylabusRepository sylabusRepository;
    private final CourseRepository courseRepository;
    private final SylabusMapper sylabusMapper;

    @Override
    public SylabusResponse create(SylabusCreateRequest request) {
        // Map request to entity
        Sylabus sylabus = sylabusMapper.toEntity(request);
        sylabus = sylabusRepository.save(sylabus);

        // If courseId provided, link syllabus to course
        if (request.getCourseId() != null) {
            Course course = courseRepository.findByCourseIdAndDeletedFalse(request.getCourseId())
                    .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
            course.setSylabus(sylabus);
            courseRepository.save(course);
        }

        return sylabusMapper.toResponse(sylabus);
    }

    @Override
    @Transactional(readOnly = true)
    public SylabusResponse getById(String sylabusId) {
        Sylabus sylabus = findSylabusOrThrow(sylabusId);
        return sylabusMapper.toResponse(sylabus);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SylabusResponse> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Sylabus> sylabusPage = sylabusRepository.findAllByDeletedFalse(pageable);

        return PageResponse.<SylabusResponse>builder()
                .content(sylabusMapper.toResponseList(sylabusPage.getContent()))
                .currentPage(sylabusPage.getNumber())
                .pageSize(sylabusPage.getSize())
                .totalElements(sylabusPage.getTotalElements())
                .totalPages(sylabusPage.getTotalPages())
                .first(sylabusPage.isFirst())
                .last(sylabusPage.isLast())
                .build();
    }

    @Override
    public SylabusResponse update(String sylabusId, SylabusUpdateRequest request) {
        Sylabus sylabus = findSylabusOrThrow(sylabusId);

        // Use MapStruct for partial update (null values are ignored)
        sylabusMapper.updateEntity(request, sylabus);

        sylabus = sylabusRepository.save(sylabus);
        return sylabusMapper.toResponse(sylabus);
    }

    @Override
    public void delete(String sylabusId) {
        Sylabus sylabus = findSylabusOrThrow(sylabusId);

        // Soft delete
        sylabus.setDeleted(true);
        sylabus.setDeletedAt(LocalDateTime.now());
        sylabus.setDeletedBy(getCurrentUser());

        sylabusRepository.save(sylabus);
    }

    /**
     * DRY: Extracts common syllabus lookup logic
     */
    private Sylabus findSylabusOrThrow(String sylabusId) {
        return sylabusRepository.findBySylabusIdAndDeletedFalse(sylabusId)
                .orElseThrow(() -> new AppException(ErrorCode.SYLLABUS_NOT_FOUND));
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
