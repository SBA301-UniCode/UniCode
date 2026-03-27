package com.example.unicode.service.impl;

import com.example.unicode.base.ApiResponse;
import com.example.unicode.dto.request.*;
import com.example.unicode.dto.response.EnrollmentReportResponse;
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
import com.example.unicode.service.ProcessService;
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
    private final ProcessService processService;

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

    @Override
    public boolean isbanned(EnrollementBannerRequest request) {

        Course course = courseRepository.findById(request.getCoureId()).orElseThrow(
                ()-> new AppException(ErrorCode.COURSE_NOT_FOUND)
        );
        Users users  = usersRepository.findById(request.getUserId()).orElseThrow(
                ()-> new AppException(ErrorCode.USER_NOT_FOUND)
        );
        return enrollmentRepository.existsByCourseAndLearnerAndDeleted(course,users,true);
    }

    @Override
    public void banLearner(EnrollementBannerRequest request,boolean ban) {
        Course course = courseRepository.findById(request.getCoureId()).orElseThrow(
                ()-> new AppException(ErrorCode.COURSE_NOT_FOUND)
        );
        Users users  = usersRepository.findById(request.getUserId()).orElseThrow(
                ()-> new AppException(ErrorCode.USER_NOT_FOUND)
        );
        Enrollment enrollment = enrollmentRepository.findByCourseAndLearner(course,users);
        enrollment.setDeleted(ban);
        enrollmentRepository.save(enrollment);
    }

    @Override
    public Page<EnrollmentReportResponse> getReportEnrollmentFollowByCourse(UUID coureId, String keySearch,boolean banned, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt"));
        Course course = courseRepository.findById(coureId).orElseThrow(
                ()-> new AppException(ErrorCode.COURSE_NOT_FOUND)
        );
        Specification<Enrollment> specification = Specification.allOf(
          EnrollmentSpecification.findBySearchKeyLeaner(keySearch),
          EnrollmentSpecification.findByCourseId(coureId),
                EnrollmentSpecification.findbyDeleted(banned)
        );
        Page<Enrollment> enrollments = enrollmentRepository.findAll(specification,pageable);
        Page<EnrollmentReportResponse> responses = enrollments.map(enrollmentMapper::entityToReport);
        for (EnrollmentReportResponse response : responses.getContent()) {
            response.setPercentComplete(processService.getProcessOfCourses(TrackingRequest.builder()
                            .enrollmentId(response.getEnrollmentId())
                            .id(response.getCourseResponse().getCourseId())
                            .build()).getPercentComplete());
        }
        return responses;
    }


}
