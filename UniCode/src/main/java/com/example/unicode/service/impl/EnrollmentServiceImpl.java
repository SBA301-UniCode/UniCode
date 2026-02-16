package com.example.unicode.service.impl;

import com.example.unicode.base.ApiResponse;
import com.example.unicode.dto.request.SearchEnrollRequest;
import com.example.unicode.dto.request.SearchSubcriptionRequest;
import com.example.unicode.dto.request.UpdateEnrollmentRequest;
import com.example.unicode.dto.response.EnrolmentResponse;
import com.example.unicode.entity.Course;
import com.example.unicode.entity.Enrollment;
import com.example.unicode.entity.Users;
import com.example.unicode.enums.StatusCourse;
import com.example.unicode.enums.StatusPayment;
import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;
import com.example.unicode.mapper.EnrollmentMapper;
import com.example.unicode.repository.CourseRepository;
import com.example.unicode.repository.EnrollmentRepository;
import com.example.unicode.repository.SubcriptionRepository;
import com.example.unicode.repository.UsersRepository;
import com.example.unicode.service.EnrollmentService;
import com.example.unicode.specification.EnrollmentSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {
    private final EnrollmentMapper enrollmentMapper;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UsersRepository usersRepository;
    private final SubcriptionRepository subcriptionRepository;

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
       if(!subcriptionRepository.existsByLearnerAndCourseAndStatusPayment(users,course, StatusPayment.SUCCESS) && course.getPrice() > 0)
       {
           throw new AppException(ErrorCode.NOT_PAYMNET);
       }
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

    @Override
    public Page<EnrolmentResponse> search(SearchEnrollRequest request, int page, int size) {

        Specification<Enrollment> spe = Specification.allOf(
                EnrollmentSpecification.searchKey(request.getKeysearch()),
                EnrollmentSpecification.findByStatus(request.getStatusCourse()),
                EnrollmentSpecification.findByCourseId(request.getCourseId()),
                EnrollmentSpecification.findByLearnerId(request.getLeanerId())
        );
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt"));
        return enrollmentRepository.findAll(spe,pageable).map(enrollmentMapper::entityToResponse);
    }

    @Override
    public Page<EnrolmentResponse> getAllByCourse(UUID courseId, int page, int size) {
        Course course = courseRepository.findById(courseId).orElseThrow(
                ()-> new AppException(ErrorCode.COURSE_NOT_FOUND)
        );
        return enrollmentRepository.findByCourse(course,PageRequest.of(page,size,Sort.by("createdAt"))).map(enrollmentMapper::entityToResponse);
    }

    @Override
    public Page<EnrolmentResponse> getAllByLearner(UUID userId, int page, int size) {
        Users users = usersRepository.findById(userId).orElseThrow(
                ()-> new AppException(ErrorCode.USER_NOT_FOUND)
        );
        return enrollmentRepository.getByLearner(users,PageRequest.of(page,size,Sort.by("createdAt"))).map(enrollmentMapper::entityToResponse);
    }

    @Override
    public Page<EnrolmentResponse> myLearn(StatusCourse  statusCourse, int page, int size) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users users = usersRepository.findByEmail(email);
        if(users == null)
        {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        return enrollmentRepository.getByLearnerAndStatusCourse(users,statusCourse,PageRequest.of(page,size,Sort.by("createdAt"))).map(enrollmentMapper::entityToResponse);
    }

    @Override
    public EnrolmentResponse update(UpdateEnrollmentRequest request) {
        Enrollment enrollment = enrollmentRepository.findById(request.getEnrollmentId()).orElseThrow(
                ()-> new AppException(ErrorCode.ENROLLMENT_NOT_FOUND)
        );
        enrollment.setStatusCourse(request.getStatusCourse());
        return enrollmentMapper.entityToResponse(enrollmentRepository.save(enrollment));
    }


}
