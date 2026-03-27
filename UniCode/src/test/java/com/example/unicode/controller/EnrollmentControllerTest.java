package com.example.unicode.controller;

import com.example.unicode.service.EnrollmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnrollmentControllerTest {

    @Mock
    private EnrollmentService enrollmentService;

    @InjectMocks
    private EnrollmentController controller;

    @Test
    void isEnrolledShouldDelegate() {
        UUID courseId = UUID.randomUUID();
        when(enrollmentService.isEnrolled(courseId)).thenReturn(true);

        var response = controller.isEnrolled(courseId);

        assertEquals(200, response.getStatusCode().value());
        verify(enrollmentService).isEnrolled(courseId);
    }
}

