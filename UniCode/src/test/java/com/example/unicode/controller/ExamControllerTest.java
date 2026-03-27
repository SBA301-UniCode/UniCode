package com.example.unicode.controller;

import com.example.unicode.dto.response.ExamResponse;
import com.example.unicode.service.ExamService;
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
class ExamControllerTest {

    @Mock
    private ExamService examService;

    @InjectMocks
    private ExamController controller;

    @Test
    void getExamByIdShouldDelegate() {
        UUID examId = UUID.randomUUID();
        when(examService.getExamById(examId)).thenReturn(new ExamResponse());

        var response = controller.getExamById(examId);

        assertEquals(200, response.getStatusCode().value());
        verify(examService).getExamById(examId);
    }
}

