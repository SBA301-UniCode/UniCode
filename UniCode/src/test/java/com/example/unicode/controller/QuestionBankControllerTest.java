package com.example.unicode.controller;

import com.example.unicode.dto.response.QuestionBankResponse;
import com.example.unicode.service.QuestionBankSerivce;
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
class QuestionBankControllerTest {

    @Mock
    private QuestionBankSerivce questionBankSerivce;

    @InjectMocks
    private QuestionBankController controller;

    @Test
    void getQuestionByIdShouldDelegate() {
        UUID id = UUID.randomUUID();
        when(questionBankSerivce.getQuestionById(id)).thenReturn(new QuestionBankResponse());

        var response = controller.getQuestionById(id);

        assertEquals(200, response.getStatusCode().value());
        verify(questionBankSerivce).getQuestionById(id);
    }
}

