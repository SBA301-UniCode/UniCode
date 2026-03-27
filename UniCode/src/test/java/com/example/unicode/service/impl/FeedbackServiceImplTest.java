package com.example.unicode.service.impl;

import com.example.unicode.dto.request.FeedbackRequest;
import com.example.unicode.entity.Feedback;
import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;
import com.example.unicode.mapper.FeedBackMapper;
import com.example.unicode.repository.CourseRepository;
import com.example.unicode.repository.EnrollmentRepository;
import com.example.unicode.repository.FeedbackRepository;
import com.example.unicode.repository.ImageRepository;
import com.example.unicode.service.UserService;
import com.example.unicode.ultils.CloudiaryUltils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceImplTest {

    @Mock
    private FeedbackRepository feedbackRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private CloudiaryUltils cloudiaryUltils;
    @Mock
    private UserService userService;
    @Mock
    private FeedBackMapper feedBackMapper;
    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private FeedbackServiceImpl feedbackService;

    @Test
    void createFeedbackShouldThrowWhenCourseNotFound() {
        UUID courseId = UUID.randomUUID();
        when(feedBackMapper.requestToEntity(org.mockito.ArgumentMatchers.any())).thenReturn(new Feedback());
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class,
                () -> feedbackService.createFeedback(courseId, new FeedbackRequest(), null));

        assertEquals(ErrorCode.COURSE_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void canEditAndDeleteShouldThrowWhenFeedbackNotFound() {
        UUID feedbackId = UUID.randomUUID();
        when(feedbackRepository.findById(feedbackId)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> feedbackService.canEditAndDelete(feedbackId));

        assertEquals(ErrorCode.FEEDBACK_NOT_FOUND, ex.getErrorCode());
    }
}

