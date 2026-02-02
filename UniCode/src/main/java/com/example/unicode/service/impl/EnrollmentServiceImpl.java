package com.example.unicode.service.impl;


import com.example.unicode.dto.request.EnrollmentRequest;
import com.example.unicode.entity.Content;
import com.example.unicode.entity.Course;
import com.example.unicode.entity.Enrollment;
import com.example.unicode.entity.Users;
import com.example.unicode.enums.StatusContent;
import com.example.unicode.enums.StatusCourse;
import com.example.unicode.repository.ContentRepo;
import com.example.unicode.repository.CourseRepo;
import com.example.unicode.repository.EnrollmentRepo;
import com.example.unicode.repository.UsersRepo;
import com.example.unicode.service.EnrollmentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepo enrollmentRepo;
    private final UsersRepo usersRepo;
    private final CourseRepo courseRepo;
    private final ContentRepo contentRepo;

    @Override
    public void enrollUserToCourse(EnrollmentRequest enrollmentRequest, UUID userId) {
        Enrollment enrollment = new Enrollment();

        // Tạo đối tượng Proxy cho User (Learner) từ userId
        // Giả sử bạn đã inject UserRepo vào nhé
        Users learnerProxy = usersRepo.getReferenceById(userId);
        enrollment.setLearner(learnerProxy);

        // Tạo đối tượng Proxy cho Course từ courseId trong Request
        // Giả sử bạn đã inject CourseRepo vào nhé
        Course courseProxy = courseRepo.getReferenceById(enrollmentRequest.getCourseId());
        enrollment.setCourse(courseProxy);

        // Thiết lập các thông tin khác
        enrollment.setEnrollmentDate(LocalDateTime.now());
        enrollment.setStatusCourse(StatusCourse.COMPLETED); // Hoặc enum tương ứng của bạn

        // Lưu Enrollment
        final Enrollment savedEnrollment = enrollmentRepo.save(enrollment);

    }
}
