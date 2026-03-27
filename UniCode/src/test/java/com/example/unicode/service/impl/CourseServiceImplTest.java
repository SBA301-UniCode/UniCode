package com.example.unicode.service.impl;

import com.example.unicode.dto.request.CourseCreateRequest;
import com.example.unicode.dto.request.CourseUpdateRequest;
import com.example.unicode.dto.request.ReportRequest;
import com.example.unicode.dto.response.CourseResponse;
import com.example.unicode.dto.response.InstructorReport;
import com.example.unicode.dto.response.PageResponse;
import com.example.unicode.entity.Course;
import com.example.unicode.entity.Subcription;
import com.example.unicode.entity.Users;
import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;
import com.example.unicode.mapper.CourseMapper;
import com.example.unicode.repository.CourseRepository;
import com.example.unicode.repository.EnrollmentRepository;
import com.example.unicode.repository.SubcriptionRepository;
import com.example.unicode.repository.UsersRepository;
import com.example.unicode.ultils.CloudiaryUltils;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private UsersRepository usersRepository;
    @Mock
    private CourseMapper courseMapper;
    @Mock
    private CloudiaryUltils cloudiaryUltils;
    @Mock
    private SubcriptionRepository subcriptionRepository;
    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private CourseServiceImpl courseService;

    private UUID courseId;
    private Users instructor;
    private Course course;
    private CourseResponse courseResponse;
    private MultipartFile file;

    @BeforeEach
    void setUp() throws Exception {
        courseId = UUID.randomUUID();

        instructor = new Users();
        instructor.setUserId(UUID.randomUUID());
        instructor.setEmail("instructor@test.com");

        course = new Course();
        course.setCourseId(courseId);
        course.setTitle("Test Course");
        course.setInstructors(instructor);

        courseResponse = new CourseResponse();
        courseResponse.setCourseId(courseId);
        courseResponse.setTitle("Test Course");

        file = mock(MultipartFile.class);
        
        // Setup SecurityContext
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.isAuthenticated()).thenReturn(true);
        lenient().when(authentication.getName()).thenReturn("instructor@test.com");
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void test_1() throws Exception {
        SecurityContextHolder.clearContext();
    }

    // --- create ---
    
    @Test
    void test_2() throws Exception {
        CourseCreateRequest request = new CourseCreateRequest();
        request.setInstructorId(instructor.getUserId());
        
        when(usersRepository.findByUserIdAndDeletedFalse(request.getInstructorId()))
                .thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> courseService.create(request, file));
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void test_3() throws Exception {
        CourseCreateRequest request = new CourseCreateRequest();
        request.setInstructorId(instructor.getUserId());

        when(usersRepository.findByUserIdAndDeletedFalse(request.getInstructorId()))
                .thenReturn(Optional.of(instructor));
        when(courseMapper.toEntity(request)).thenReturn(course);
        when(courseRepository.save(any(Course.class))).thenReturn(course);
        when(courseMapper.toResponse(course)).thenReturn(courseResponse);

        CourseResponse result = courseService.create(request, null);

        assertNotNull(result);
        assertEquals(courseId, result.getCourseId());
        verify(cloudiaryUltils, never()).getUrlCloudiary(any(), anyString());
    }

    @Test
    void test_4() throws Exception {
        CourseCreateRequest request = new CourseCreateRequest();
        request.setInstructorId(instructor.getUserId());

        when(usersRepository.findByUserIdAndDeletedFalse(request.getInstructorId()))
                .thenReturn(Optional.of(instructor));
        when(courseMapper.toEntity(request)).thenReturn(course);
        when(cloudiaryUltils.getUrlCloudiary(file, "image"))
                .thenReturn(Arrays.asList("public_id", "image_url"));
        when(courseRepository.save(any(Course.class))).thenReturn(course);
        when(courseMapper.toResponse(course)).thenReturn(courseResponse);

        CourseResponse result = courseService.create(request, file);

        assertNotNull(result);
        assertEquals("public_id", course.getPublicId());
        assertEquals("image_url", course.getImage());
    }

    @Test
    void test_5() throws Exception {
        CourseCreateRequest request = new CourseCreateRequest();
        request.setInstructorId(instructor.getUserId());

        when(usersRepository.findByUserIdAndDeletedFalse(request.getInstructorId()))
                .thenReturn(Optional.of(instructor));
        when(courseMapper.toEntity(request)).thenReturn(course);
        when(cloudiaryUltils.getUrlCloudiary(file, "image"))
                .thenThrow(new RuntimeException("Cloudinary Error"));

        AppException exception = assertThrows(AppException.class, () -> courseService.create(request, file));
        assertEquals(ErrorCode.CAN_UPLOAD_MATERIAL, exception.getErrorCode());
    }

    // --- getById ---

    @Test
    void test_6() throws Exception {
        when(courseRepository.findByCourseIdAndDeletedFalse(courseId))
                .thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> courseService.getById(courseId));
        assertEquals(ErrorCode.COURSE_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void test_7() throws Exception {
        when(courseRepository.findByCourseIdAndDeletedFalse(courseId))
                .thenReturn(Optional.of(course));
        when(courseMapper.toResponse(course)).thenReturn(courseResponse);

        CourseResponse result = courseService.getById(courseId);

        assertNotNull(result);
        assertEquals(courseResponse.getCourseId(), result.getCourseId());
    }

    // --- getAll ---

    @Test
    void test_8() throws Exception {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<Course> coursePage = new PageImpl<>(List.of(course), pageable, 1);

        when(courseRepository.findAllByDeletedFalse(pageable)).thenReturn(coursePage);
        when(courseMapper.toResponseList(coursePage.getContent())).thenReturn(List.of(courseResponse));

        PageResponse<CourseResponse> result = courseService.getAll(page, size);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(1, result.getContent().size());
    }

    // --- update ---

    @Test
    void test_9() throws Exception {
        CourseUpdateRequest request = new CourseUpdateRequest();
        
        when(courseRepository.findByCourseIdAndDeletedFalse(courseId))
                .thenReturn(Optional.of(course));
        doNothing().when(courseMapper).updateEntity(request, course);
        when(courseRepository.save(course)).thenReturn(course);
        when(courseMapper.toResponse(course)).thenReturn(courseResponse);

        CourseResponse result = courseService.update(courseId, request, null);

        assertNotNull(result);
        verify(cloudiaryUltils, never()).getUrlCloudiary(any(), anyString());
    }

    @Test
    void test_10() throws Exception {
        CourseUpdateRequest request = new CourseUpdateRequest();
        
        when(courseRepository.findByCourseIdAndDeletedFalse(courseId))
                .thenReturn(Optional.of(course));
        doNothing().when(courseMapper).updateEntity(request, course);
        when(file.isEmpty()).thenReturn(false);
        when(cloudiaryUltils.getUrlCloudiary(file, "image"))
                .thenReturn(Arrays.asList("public_id_new", "image_url_new"));
        when(courseRepository.save(course)).thenReturn(course);
        when(courseMapper.toResponse(course)).thenReturn(courseResponse);

        CourseResponse result = courseService.update(courseId, request, file);

        assertNotNull(result);
        assertEquals("public_id_new", course.getPublicId());
        assertEquals("image_url_new", course.getImage());
    }

    @Test
    void test_11() throws Exception {
        CourseUpdateRequest request = new CourseUpdateRequest();
        
        when(courseRepository.findByCourseIdAndDeletedFalse(courseId))
                .thenReturn(Optional.of(course));
        doNothing().when(courseMapper).updateEntity(request, course);
        when(file.isEmpty()).thenReturn(false);
        when(cloudiaryUltils.getUrlCloudiary(file, "image"))
                .thenThrow(new RuntimeException("Cloudinary Error"));

        AppException exception = assertThrows(AppException.class, () -> courseService.update(courseId, request, file));
        assertEquals(ErrorCode.CAN_UPLOAD_MATERIAL, exception.getErrorCode());
    }

    // --- updateImage ---

    @Test
    void test_12() throws Exception {
        when(courseRepository.findByCourseIdAndDeletedFalse(courseId))
                .thenReturn(Optional.of(course));

        AppException exception = assertThrows(AppException.class, () -> courseService.updateImage(courseId, null));
        assertEquals(ErrorCode.CAN_UPLOAD_MATERIAL, exception.getErrorCode());
    }

    @Test
    void test_13() throws Exception {
        when(courseRepository.findByCourseIdAndDeletedFalse(courseId))
                .thenReturn(Optional.of(course));
        when(file.isEmpty()).thenReturn(true);

        AppException exception = assertThrows(AppException.class, () -> courseService.updateImage(courseId, file));
        assertEquals(ErrorCode.CAN_UPLOAD_MATERIAL, exception.getErrorCode());
    }

    @Test
    void test_14() throws Exception {
        when(courseRepository.findByCourseIdAndDeletedFalse(courseId))
                .thenReturn(Optional.of(course));
        when(file.isEmpty()).thenReturn(false);
        when(cloudiaryUltils.getUrlCloudiary(file, "image"))
                .thenReturn(Arrays.asList("pub_id_1", "img_url_1"));
        when(courseRepository.save(course)).thenReturn(course);
        when(courseMapper.toResponse(course)).thenReturn(courseResponse);

        CourseResponse result = courseService.updateImage(courseId, file);

        assertNotNull(result);
        assertEquals("pub_id_1", course.getPublicId());
        assertEquals("img_url_1", course.getImage());
    }

    @Test
    void test_15() throws Exception {
        when(courseRepository.findByCourseIdAndDeletedFalse(courseId))
                .thenReturn(Optional.of(course));
        when(file.isEmpty()).thenReturn(false);
        when(cloudiaryUltils.getUrlCloudiary(file, "image"))
                .thenThrow(new RuntimeException("Upload Exception"));

        AppException exception = assertThrows(AppException.class, () -> courseService.updateImage(courseId, file));
        assertEquals(ErrorCode.CAN_UPLOAD_MATERIAL, exception.getErrorCode());
    }

    // --- delete ---

    @Test
    void test_16() throws Exception {
        when(courseRepository.findByCourseIdAndDeletedFalse(courseId))
                .thenReturn(Optional.of(course));
        when(courseRepository.save(course)).thenReturn(course);

        courseService.delete(courseId);

        assertTrue(course.getDeleted());
        assertNotNull(course.getDeletedAt());
        assertEquals("instructor@test.com", course.getDeletedBy());
        verify(courseRepository, times(1)).save(course);
    }

    @Test
    void test_17() throws Exception {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        when(courseRepository.findByCourseIdAndDeletedFalse(courseId))
                .thenReturn(Optional.of(course));
        when(courseRepository.save(course)).thenReturn(course);

        courseService.delete(courseId);

        assertTrue(course.getDeleted());
        assertEquals("SYSTEM", course.getDeletedBy());
    }

    // --- instructorReport ---

    @Test
    void test_18() throws Exception {
        ReportRequest request = new ReportRequest();
        LocalDate today = LocalDate.now();
        request.setFrom(today.minusDays(7));
        request.setTo(today);

        when(usersRepository.findByEmail("instructor@test.com")).thenReturn(instructor);
        when(courseRepository.findByInstructors(instructor)).thenReturn(List.of(course));
        when(courseRepository.countByInstructors(instructor)).thenReturn(1L);
        when(subcriptionRepository.sumBySubcriptionDateAndCourseIn(any(), any(), any())).thenReturn(100.0);
        when(enrollmentRepository.countByCourseIn(any())).thenReturn(5L);

        Subcription subcription = new Subcription();
        subcription.setSubcriptionPrice(50L);
        subcription.setCreatedAt(java.time.LocalDateTime.now());
        when(subcriptionRepository.findByCreatedAtBetween(any(), any()))
                .thenReturn(List.of(subcription));

        InstructorReport result = courseService.instructorReport(request);

        assertNotNull(result);
        assertEquals(1L, result.getNumberOfCourse());
        assertEquals(100.0, result.getRevenua());
        assertEquals(5L, result.getTotalStudent());
        assertEquals(1, result.getReports().size());
        assertEquals(50.0, result.getReports().get(0).getPrice());
    }
}
