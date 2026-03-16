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
import com.example.unicode.repository.CourseRepository;
import com.example.unicode.repository.UsersRepository;
import com.example.unicode.service.CourseService;
import com.example.unicode.ultils.CloudiaryUltils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of CourseService.
 * Follows Single Responsibility Principle (SRP) - handles only course business logic.
 * Uses Dependency Injection for loose coupling (DIP).
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final UsersRepository usersRepository;
    private final CourseMapper courseMapper;
    private final CloudiaryUltils cloudiaryUltils;

    @Override
    public CourseResponse create(CourseCreateRequest request, MultipartFile file) {
        // Validate instructor exists
        Users instructor = usersRepository.findByUserIdAndDeletedFalse(request.getInstructorId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Map request to entity
        Course course = courseMapper.toEntity(request);
        course.setInstructors(instructor);
        if(file != null) {
            try {
                List<String> list = cloudiaryUltils.getUrlCloudiary(file,"image");
                if(list.size() == 2){
                    course.setImage(list.get(1));
                    course.setPublicId(list.get(0));
                }
            }catch (Exception e)
            {
               throw  new AppException(ErrorCode.CAN_UPLOAD_MATERIAL);
            }

        }
        course = courseRepository.save(course);
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
        Page<Course> coursePage = courseRepository.findAllByDeletedFalse(pageable);

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

        course = courseRepository.save(course);
        return courseMapper.toResponse(course);
    }

    @Override
    public void delete(UUID courseId) {
        Course course = findCourseOrThrow(courseId);

        // Soft delete
        course.setDeleted(true);
        course.setDeletedAt(LocalDateTime.now());
        course.setDeletedBy(getCurrentUser());

        courseRepository.save(course);
    }

    /**
     * DRY: Extracts common course lookup logic
     */
    private Course findCourseOrThrow(UUID courseId) {
        return courseRepository.findByCourseIdAndDeletedFalse(courseId)
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
