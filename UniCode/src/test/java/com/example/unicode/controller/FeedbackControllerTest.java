package com.example.unicode.controller;

import com.example.unicode.service.FeedbackService;
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
class FeedbackControllerTest {

    @Mock
    private FeedbackService feedbackService;

    @InjectMocks
    private FeedbackController controller;

    @Test
    void canFeedbackShouldDelegate() {
        UUID courseId = UUID.randomUUID();
        when(feedbackService.canFeedback(courseId)).thenReturn(true);

        var response = controller.canFeedback(courseId);

        assertEquals(200, response.getStatusCode().value());
        verify(feedbackService).canFeedback(courseId);
    }
}

