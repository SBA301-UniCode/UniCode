package com.example.unicode.service.impl;

import com.example.unicode.dto.request.SearchEnrollRequest;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceImplTest {

    @Mock
    private EnrollmentMapper enrollmentMapper;
    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private UsersRepository usersRepository;
    @Mock
    private SubcriptionRepository subcriptionRepository;

    @InjectMocks
    private EnrollmentServiceImpl enrollmentService;

    private UUID courseId;
    private UUID userId;
    private UUID enrollmentId;
    private Users user;
    private Course course;
    private Enrollment enrollment;
    private EnrolmentResponse enrolmentResponse;

    @BeforeEach
    void setUp() {
        courseId = UUID.randomUUID();
        userId = UUID.randomUUID();
        enrollmentId = UUID.randomUUID();

        user = new Users();
        user.setUserId(userId);
        user.setEmail("learner@test.com");

        course = new Course();
        course.setCourseId(courseId);
        course.setPrice(100.0);

        enrollment = new Enrollment();
        enrollment.setEnrollmentId(enrollmentId);
        enrollment.setCourse(course);
        enrollment.setLearner(user);

        enrolmentResponse = EnrolmentResponse.builder().enrollmentId(enrollmentId).build();

        setupSecurityContext("learner@test.com");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void setupSecurityContext(String username) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn(username);
        SecurityContextHolder.setContext(securityContext);
    }

    // --- joinCousera ---

    @Test
    void joinCousera_ShouldThrowAppException_WhenUserNotFound() throws Exception {
        when(usersRepository.findByEmail("learner@test.com")).thenReturn(null);

        AppException ex = assertThrows(AppException.class, () -> enrollmentService.joinCousera(courseId));
        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void joinCousera_ShouldThrowAppException_WhenCourseNotFound() throws Exception {
        when(usersRepository.findByEmail("learner@test.com")).thenReturn(user);
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> enrollmentService.joinCousera(courseId));
        assertEquals(ErrorCode.COURSE_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void joinCousera_ShouldThrowAppException_WhenNotPaidAndPriceGreaterThanZero() throws Exception {
        when(usersRepository.findByEmail("learner@test.com")).thenReturn(user);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(subcriptionRepository.existsByLearnerAndCourseAndStatusPayment(user, course, StatusPayment.SUCCESS))
                .thenReturn(false);

        AppException ex = assertThrows(AppException.class, () -> enrollmentService.joinCousera(courseId));
        assertEquals(ErrorCode.NOT_PAYMNET, ex.getErrorCode());
    }

    @Test
    void joinCousera_ShouldCreateEnrollment_WhenFreeCourse() throws Exception {
        course.setPrice(0.0);
        when(usersRepository.findByEmail("learner@test.com")).thenReturn(user);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(enrollment);
        when(enrollmentMapper.entityToResponse(enrollment)).thenReturn(enrolmentResponse);

        EnrolmentResponse result = enrollmentService.joinCousera(courseId);

        assertNotNull(result);
        assertEquals(enrollmentId, result.getEnrollmentId());
    }

    @Test
    void joinCousera_ShouldCreateEnrollment_WhenPaidCourseAndSubscriptionExists() throws Exception {
        course.setPrice(100.0);
        when(usersRepository.findByEmail("learner@test.com")).thenReturn(user);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(subcriptionRepository.existsByLearnerAndCourseAndStatusPayment(user, course, StatusPayment.SUCCESS))
                .thenReturn(true);
        
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(enrollment);
        when(enrollmentMapper.entityToResponse(enrollment)).thenReturn(enrolmentResponse);

        EnrolmentResponse result = enrollmentService.joinCousera(courseId);

        assertNotNull(result);
        assertEquals(enrollmentId, result.getEnrollmentId());
    }

    // --- isEnrolled ---

    @Test
    void isEnrolled_ShouldThrowAppException_WhenUserNotFound() throws Exception {
        when(usersRepository.findByEmail("learner@test.com")).thenReturn(null);

        AppException ex = assertThrows(AppException.class, () -> enrollmentService.isEnrolled(courseId));
        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void isEnrolled_ShouldThrowAppException_WhenCourseNotFound() throws Exception {
        when(usersRepository.findByEmail("learner@test.com")).thenReturn(user);
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> enrollmentService.isEnrolled(courseId));
        assertEquals(ErrorCode.COURSE_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void isEnrolled_ShouldReturnTrue_WhenEnrolled() throws Exception {
        when(usersRepository.findByEmail("learner@test.com")).thenReturn(user);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByCourseAndLearner(course, user)).thenReturn(true);

        boolean result = enrollmentService.isEnrolled(courseId);

        assertTrue(result);
    }

    @Test
    void isEnrolled_ShouldReturnFalse_WhenNotEnrolled() throws Exception {
        when(usersRepository.findByEmail("learner@test.com")).thenReturn(user);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByCourseAndLearner(course, user)).thenReturn(false);

        boolean result = enrollmentService.isEnrolled(courseId);

        assertFalse(result);
    }

    // --- search ---

    @Test
    void search_ShouldReturnPageResponse() throws Exception {
        SearchEnrollRequest request = new SearchEnrollRequest();
        request.setKeysearch("test");
        request.setStatusCourse(StatusCourse.IN_PROGRESS);
        request.setCourseId(courseId);
        request.setLeanerId(userId);

        Page<Enrollment> page = new PageImpl<>(List.of(enrollment));
        when(enrollmentRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(enrollmentMapper.entityToResponse(enrollment)).thenReturn(enrolmentResponse);

        Page<EnrolmentResponse> result = enrollmentService.search(request, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(enrollmentId, result.getContent().get(0).getEnrollmentId());
    }

    // --- getAllByCourse ---

    @Test
    void getAllByCourse_ShouldThrowAppException_WhenCourseNotFound() throws Exception {
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> enrollmentService.getAllByCourse(courseId, 0, 10));
        assertEquals(ErrorCode.COURSE_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void getAllByCourse_ShouldReturnPageResponse() throws Exception {
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        Page<Enrollment> page = new PageImpl<>(List.of(enrollment));
        when(enrollmentRepository.findByCourse(eq(course), any(Pageable.class))).thenReturn(page);
        when(enrollmentMapper.entityToResponse(enrollment)).thenReturn(enrolmentResponse);

        Page<EnrolmentResponse> result = enrollmentService.getAllByCourse(courseId, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(enrollmentId, result.getContent().get(0).getEnrollmentId());
    }

    // --- getAllByLearner ---

    @Test
    void getAllByLearner_ShouldThrowAppException_WhenUserNotFound() throws Exception {
        when(usersRepository.findById(userId)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> enrollmentService.getAllByLearner(userId, 0, 10));
        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void getAllByLearner_ShouldReturnPageResponse() throws Exception {
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));
        Page<Enrollment> page = new PageImpl<>(List.of(enrollment));
        when(enrollmentRepository.getByLearner(eq(user), any(Pageable.class))).thenReturn(page);
        when(enrollmentMapper.entityToResponse(enrollment)).thenReturn(enrolmentResponse);

        Page<EnrolmentResponse> result = enrollmentService.getAllByLearner(userId, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(enrollmentId, result.getContent().get(0).getEnrollmentId());
    }

    // --- myLearn ---

    @Test
    void myLearn_ShouldThrowAppException_WhenUserNotFound() throws Exception {
        when(usersRepository.findByEmail("learner@test.com")).thenReturn(null);

        AppException ex = assertThrows(AppException.class, () -> enrollmentService.myLearn(StatusCourse.IN_PROGRESS, 0, 10));
        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void myLearn_ShouldReturnPageResponse() throws Exception {
        when(usersRepository.findByEmail("learner@test.com")).thenReturn(user);
        Page<Enrollment> page = new PageImpl<>(List.of(enrollment));
        when(enrollmentRepository.getByLearnerAndStatusCourse(eq(user), eq(StatusCourse.IN_PROGRESS), any(Pageable.class))).thenReturn(page);
        when(enrollmentMapper.entityToResponse(enrollment)).thenReturn(enrolmentResponse);

        Page<EnrolmentResponse> result = enrollmentService.myLearn(StatusCourse.IN_PROGRESS, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(enrollmentId, result.getContent().get(0).getEnrollmentId());
    }

    // --- update ---

    @Test
    void update_ShouldThrowAppException_WhenEnrollmentNotFound() throws Exception {
        UpdateEnrollmentRequest request = new UpdateEnrollmentRequest();
        request.setEnrollmentId(enrollmentId);

        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> enrollmentService.update(request));
        assertEquals(ErrorCode.ENROLLMENT_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void update_ShouldUpdateStatusAndReturnResponse() throws Exception {
        UpdateEnrollmentRequest request = new UpdateEnrollmentRequest();
        request.setEnrollmentId(enrollmentId);
        request.setStatusCourse(StatusCourse.COMPLETED);

        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));
        when(enrollmentRepository.save(enrollment)).thenReturn(enrollment);
        when(enrollmentMapper.entityToResponse(enrollment)).thenReturn(enrolmentResponse);

        EnrolmentResponse result = enrollmentService.update(request);

        assertNotNull(result);
        assertEquals(StatusCourse.COMPLETED, enrollment.getStatusCourse());
        assertEquals(enrollmentId, result.getEnrollmentId());
    }
}
