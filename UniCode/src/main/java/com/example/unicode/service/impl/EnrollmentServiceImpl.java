package com.example.unicode.service.impl;

import com.example.unicode.dto.response.EnrolmentResponse;
import com.example.unicode.entity.Course;
import com.example.unicode.entity.Enrollment;
import com.example.unicode.entity.Users;
import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;
import com.example.unicode.mapper.EnrollmentMapper;
import com.example.unicode.repository.CourseRepository;
import com.example.unicode.repository.EnrollmentRepository;
import com.example.unicode.repository.UsersRepository;
import com.example.unicode.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentMapper enrollmentMapper;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UsersRepository usersRepository;

    @Override
    public EnrolmentResponse joinCousera(UUID courseId) {
       String email = SecurityContextHolder.getContext().getAuthentication().getName();
       Users users = usersRepository.findByEmail(email);
       if(users == null)
       {
           throw new AppException(ErrorCode.USER_NOT_FOUND);
       }
        Course course = courseRepository.findById(courseId).orElseThrow(
                ()-> new AppException(ErrorCode.COURSE_NOT_FOUND)
        );
        Enrollment enrollment = Enrollment.builder()
                .course(course)
                .learner(users)
                .build();

        return enrollmentMapper.entityToResponse(enrollmentRepository.save(enrollment));
    }

    @Override
    public boolean isEnrolled(UUID courseId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users users = usersRepository.findByEmail(email);
        if(users == null)
        {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        Course course = courseRepository.findById(courseId).orElseThrow(
                ()-> new AppException(ErrorCode.COURSE_NOT_FOUND)
        );

        return enrollmentRepository.existsByCourseAndLearner(course,users);
    }
}
