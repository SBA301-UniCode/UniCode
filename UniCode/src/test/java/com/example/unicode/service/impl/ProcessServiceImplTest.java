package com.example.unicode.service.impl;

import com.example.unicode.dto.request.ProcessRequest;
import com.example.unicode.dto.request.TrackingRequest;
import com.example.unicode.dto.response.TrackingResponse;
import com.example.unicode.dto.response.TrackingResponse.ProcessResponse;
import com.example.unicode.entity.*;
import com.example.unicode.entity.Process;
import com.example.unicode.enums.StatusContent;
import com.example.unicode.enums.StatusCourse;
import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;
import com.example.unicode.mapper.ProcessMapper;
import com.example.unicode.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessServiceImplTest {

    @Mock
    private ProcessRepository processRepository;
    @Mock
    private LessonRepository lessonRepository;
    @Mock
    private ProcessMapper processMapper;
    @Mock
    private ChapterRepository chapterRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private ContentRepo contentRepo;

    @InjectMocks
    private ProcessServiceImpl processService;

    private UUID enrollmentId;
    private UUID contentId;
    private UUID lessonId;
    private UUID chapterId;
    private UUID courseId;

    private Enrollment enrollment;
    private Content content;
    private Lesson lesson;
    private Chapter chapter;
    private Course course;
    private Process processEntity;
    private ProcessResponse processResponse;

    @BeforeEach
    void setUp() {
        enrollmentId = UUID.randomUUID();
        contentId = UUID.randomUUID();
        lessonId = UUID.randomUUID();
        chapterId = UUID.randomUUID();
        courseId = UUID.randomUUID();

        course = new Course();
        course.setCourseId(courseId);

        chapter = new Chapter();
        chapter.setChapterId(chapterId);
        chapter.setCourse(course);

        lesson = new Lesson();
        lesson.setLessonId(lessonId);
        lesson.setChapter(chapter);

        content = new Content();
        content.setContentId(contentId);
        content.setLesson(lesson);

        enrollment = new Enrollment();
        enrollment.setEnrollmentId(enrollmentId);

        processEntity = new Process();
        processEntity.setEnrollment(enrollment);

        processResponse = ProcessResponse.builder().statusContent(StatusContent.COMPLETED).build();
    }

    // --- trackProcessContent ---

    @Test
    void trackProcessContent_ShouldThrowAppException_WhenEnrollmentNotFound() {
        ProcessRequest request = new ProcessRequest();
        request.setEnrollmentId(enrollmentId);

        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> processService.trackProcessContent(request));
        assertEquals(ErrorCode.ENROLLMENT_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void trackProcessContent_ShouldThrowAppException_WhenContentNotFound() {
        ProcessRequest request = new ProcessRequest();
        request.setEnrollmentId(enrollmentId);
        request.setContentId(contentId);

        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));
        when(contentRepo.findById(contentId)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> processService.trackProcessContent(request));
        assertEquals(ErrorCode.CONTENT_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void trackProcessContent_ShouldTrackAndCreateNewProcesses() {
        ProcessRequest request = new ProcessRequest();
        request.setEnrollmentId(enrollmentId);
        request.setContentId(contentId);
        request.setStatus(StatusContent.COMPLETED);

        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));
        when(contentRepo.findById(contentId)).thenReturn(Optional.of(content));

        // existing process is null, so it creates one
        when(processRepository.findByContentAndEnrollment(content, enrollment)).thenReturn(null);

        // For processStatusLesson tracking response
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(contentRepo.findAllByLessonAndDeleted(lesson, false)).thenReturn(List.of(content));
        // Mock getProcessOfLesson mapping
        Process processContentForLesson = new Process(); // returned by repo
        when(processRepository.findByContentAndEnrollment_EnrollmentId(content, enrollmentId))
                .thenReturn(processContentForLesson);
        when(processMapper.entityToResponse(processContentForLesson)).thenReturn(processResponse);

        // lesson process is null
        when(processRepository.findByLessonAndEnrollment(lesson, enrollment)).thenReturn(null);

        // For processStatusChapter tracking response
        when(chapterRepository.findById(chapterId)).thenReturn(Optional.of(chapter));
        when(lessonRepository.findByChapterAndDeleted(chapter, false)).thenReturn(List.of(lesson));
        Process processLessonForChapter = new Process();
        when(processRepository.findByLessonAndEnrollment_EnrollmentId(lesson, enrollmentId))
                .thenReturn(processLessonForChapter);
        when(processMapper.entityToResponse(processLessonForChapter)).thenReturn(processResponse);

        // chapter process is null
        when(processRepository.findByChapterAndEnrollment(chapter, enrollment)).thenReturn(null);

        // final trackProcessContent save
        when(processRepository.save(any(Process.class))).thenReturn(processEntity);
        when(processMapper.entityToResponse(processEntity)).thenReturn(processResponse);

        ProcessResponse result = processService.trackProcessContent(request);

        assertNotNull(result);
        assertEquals(StatusContent.COMPLETED, result.getStatusContent());
        
        // Verifications
        verify(processRepository, times(3)).save(any(Process.class)); // 1 for lesson, 1 for chapter, 1 for content
    }

    @Test
    void trackProcessContent_ShouldTrackAndMapWithExistingProcesses() {
        ProcessRequest request = new ProcessRequest();
        request.setEnrollmentId(enrollmentId);
        request.setContentId(contentId);
        request.setStatus(StatusContent.IN_PROCESS);

        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));
        when(contentRepo.findById(contentId)).thenReturn(Optional.of(content));

        // existing process exists
        when(processRepository.findByContentAndEnrollment(content, enrollment)).thenReturn(processEntity);

        // For processStatusLesson tracking response
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(contentRepo.findAllByLessonAndDeleted(lesson, false)).thenReturn(Collections.emptyList());

        // lesson process exists
        when(processRepository.findByLessonAndEnrollment(lesson, enrollment)).thenReturn(processEntity);

        // For processStatusChapter tracking response
        when(chapterRepository.findById(chapterId)).thenReturn(Optional.of(chapter));
        when(lessonRepository.findByChapterAndDeleted(chapter, false)).thenReturn(Collections.emptyList());

        // chapter process exists
        when(processRepository.findByChapterAndEnrollment(chapter, enrollment)).thenReturn(processEntity);

        // final trackProcessContent save
        when(processRepository.save(any(Process.class))).thenReturn(processEntity);
        ProcessResponse inProcessResponse = ProcessResponse.builder().statusContent(StatusContent.IN_PROCESS).build();
        when(processMapper.entityToResponse(processEntity)).thenReturn(inProcessResponse);

        ProcessResponse result = processService.trackProcessContent(request);

        assertNotNull(result);
        assertEquals(StatusContent.IN_PROCESS, result.getStatusContent());
        assertEquals(StatusContent.IN_PROCESS, processEntity.getStatusContent()); // updated internally
    }

    // --- getProcessOfLesson ---

    @Test
    void getProcessOfLesson_ShouldThrowAppException_WhenLessonNotFound() {
        TrackingRequest request = TrackingRequest.builder().id(lessonId).build();
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> processService.getProcessOfLesson(request));
        assertEquals(ErrorCode.LESSON_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void getProcessOfLesson_ShouldReturnZeroPercent_WhenNoContents() {
        TrackingRequest request = TrackingRequest.builder().id(lessonId).enrollmentId(enrollmentId).build();
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(contentRepo.findAllByLessonAndDeleted(lesson, false)).thenReturn(null);

        TrackingResponse response = processService.getProcessOfLesson(request);
        assertEquals(0.0, response.getPercentComplete());
        assertTrue(response.getProcessResponseList().isEmpty());
    }

    @Test
    void getProcessOfLesson_ShouldReturnPercentAndSetId_WhenNullProcessResponse() {
        // Line 100: if (processResponse != null) ... we should cover when it is null
        TrackingRequest request = TrackingRequest.builder().id(lessonId).enrollmentId(enrollmentId).build();
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(contentRepo.findAllByLessonAndDeleted(lesson, false)).thenReturn(List.of(content));

        when(processRepository.findByContentAndEnrollment_EnrollmentId(content, enrollmentId)).thenReturn(processEntity);
        // Map returns null
        when(processMapper.entityToResponse(processEntity)).thenReturn(null);

        TrackingResponse response = processService.getProcessOfLesson(request);
        assertEquals(0.0, response.getPercentComplete());
    }

    @Test
    void getProcessOfLesson_ShouldCountInProcess_WhenStatusIsInProcess() {
        TrackingRequest request = TrackingRequest.builder().id(lessonId).enrollmentId(enrollmentId).build();
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(contentRepo.findAllByLessonAndDeleted(lesson, false)).thenReturn(List.of(content));

        when(processRepository.findByContentAndEnrollment_EnrollmentId(content, enrollmentId)).thenReturn(processEntity);
        ProcessResponse pr = ProcessResponse.builder().statusContent(StatusContent.IN_PROCESS).build();
        when(processMapper.entityToResponse(processEntity)).thenReturn(pr);

        TrackingResponse response = processService.getProcessOfLesson(request);
        assertEquals(0.0, response.getPercentComplete()); // 0 success out of 1
        assertEquals(1, response.getProcessResponseList().size());
    }


    // --- getProcessOfChapter ---

    @Test
    void getProcessOfChapter_ShouldThrowAppException_WhenChapterNotFound() {
        TrackingRequest request = TrackingRequest.builder().id(chapterId).build();
        when(chapterRepository.findById(chapterId)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> processService.getProcessOfChapter(request));
        assertEquals(ErrorCode.CHAPTER_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void getProcessOfChapter_ShouldReturnZeroPercent_WhenNoLessons() {
        TrackingRequest request = TrackingRequest.builder().id(chapterId).enrollmentId(enrollmentId).build();
        when(chapterRepository.findById(chapterId)).thenReturn(Optional.of(chapter));
        when(lessonRepository.findByChapterAndDeleted(chapter, false)).thenReturn(null);

        TrackingResponse response = processService.getProcessOfChapter(request);
        assertEquals(0.0, response.getPercentComplete());
        assertTrue(response.getProcessResponseList().isEmpty());
    }

    @Test
    void getProcessOfChapter_ShouldHandleNullProcessResponse() {
        TrackingRequest request = TrackingRequest.builder().id(chapterId).enrollmentId(enrollmentId).build();
        when(chapterRepository.findById(chapterId)).thenReturn(Optional.of(chapter));
        when(lessonRepository.findByChapterAndDeleted(chapter, false)).thenReturn(List.of(lesson));

        when(processRepository.findByLessonAndEnrollment_EnrollmentId(lesson, enrollmentId)).thenReturn(processEntity);
        when(processMapper.entityToResponse(processEntity)).thenReturn(null);

        TrackingResponse response = processService.getProcessOfChapter(request);
        assertEquals(0.0, response.getPercentComplete());
    }

    @Test
    void getProcessOfChapter_ShouldCountInProcess() {
        TrackingRequest request = TrackingRequest.builder().id(chapterId).enrollmentId(enrollmentId).build();
        when(chapterRepository.findById(chapterId)).thenReturn(Optional.of(chapter));
        when(lessonRepository.findByChapterAndDeleted(chapter, false)).thenReturn(List.of(lesson));

        when(processRepository.findByLessonAndEnrollment_EnrollmentId(lesson, enrollmentId)).thenReturn(processEntity);
        ProcessResponse pr = ProcessResponse.builder().statusContent(StatusContent.IN_PROCESS).build();
        when(processMapper.entityToResponse(processEntity)).thenReturn(pr);

        TrackingResponse response = processService.getProcessOfChapter(request);
        assertEquals(0.0, response.getPercentComplete());
        assertEquals(1, response.getProcessResponseList().size());
    }


    // --- getProcessOfCourses ---

    @Test
    void getProcessOfCourses_ShouldThrowAppException_WhenCourseNotFound() {
        TrackingRequest request = TrackingRequest.builder().id(courseId).build();
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> processService.getProcessOfCourses(request));
        assertEquals(ErrorCode.COURSE_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void getProcessOfCourses_ShouldThrowAppException_WhenEnrollmentNotFound() {
        TrackingRequest request = TrackingRequest.builder().id(courseId).enrollmentId(enrollmentId).build();
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(chapterRepository.findALlByCourseAndDeleted(course, false)).thenReturn(Collections.emptyList());
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> processService.getProcessOfCourses(request));
        assertEquals(ErrorCode.ENROLLMENT_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void getProcessOfCourses_ShouldCalculatePercentAndSetEnrollmentStatus_When100Percent() {
        TrackingRequest request = TrackingRequest.builder().id(courseId).enrollmentId(enrollmentId).build();
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(chapterRepository.findALlByCourseAndDeleted(course, false)).thenReturn(List.of(chapter));
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));

        when(processRepository.findByChapterAndEnrollment_EnrollmentId(chapter, enrollmentId)).thenReturn(processEntity);
        ProcessResponse pr = ProcessResponse.builder().statusContent(StatusContent.COMPLETED).build(); // 1 out of 1 is 100%
        when(processMapper.entityToResponse(processEntity)).thenReturn(pr);

        TrackingResponse response = processService.getProcessOfCourses(request);

        assertEquals(100.0, response.getPercentComplete());
        assertEquals(StatusCourse.COMPLETED, enrollment.getStatusCourse());
        verify(enrollmentRepository).save(enrollment);
    }

    @Test
    void getProcessOfCourses_ShouldHandleNullProcessResponse() {
        TrackingRequest request = TrackingRequest.builder().id(courseId).enrollmentId(enrollmentId).build();
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(chapterRepository.findALlByCourseAndDeleted(course, false)).thenReturn(List.of(chapter));
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));

        when(processRepository.findByChapterAndEnrollment_EnrollmentId(chapter, enrollmentId)).thenReturn(processEntity);
        when(processMapper.entityToResponse(processEntity)).thenReturn(null);

        TrackingResponse response = processService.getProcessOfCourses(request);

        assertEquals(0.0, response.getPercentComplete());
        assertEquals(StatusCourse.IN_PROGRESS, enrollment.getStatusCourse());
        verify(enrollmentRepository).save(enrollment);
    }

    @Test
    void getProcessOfCourses_ShouldHandleInProcess() {
        TrackingRequest request = TrackingRequest.builder().id(courseId).enrollmentId(enrollmentId).build();
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(chapterRepository.findALlByCourseAndDeleted(course, false)).thenReturn(List.of(chapter));
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));

        when(processRepository.findByChapterAndEnrollment_EnrollmentId(chapter, enrollmentId)).thenReturn(processEntity);
        ProcessResponse pr = ProcessResponse.builder().statusContent(StatusContent.IN_PROCESS).build();
        when(processMapper.entityToResponse(processEntity)).thenReturn(pr);

        TrackingResponse response = processService.getProcessOfCourses(request);

        assertEquals(0.0, response.getPercentComplete());
    }
}
