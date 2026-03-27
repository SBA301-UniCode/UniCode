package com.example.unicode.service.impl;

import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;
import com.example.unicode.mapper.QuestionBankMapper;
import com.example.unicode.mapper.QuestionOptionMapper;
import com.example.unicode.repository.LessonRepository;
import com.example.unicode.repository.QuestionBankRepository;
import com.example.unicode.repository.QuestionOptionRepository;
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
class QuestionBankServiceImplTest {

    @Mock
    private QuestionBankMapper questionBankMapper;
    @Mock
    private QuestionBankRepository questionBankRepository;
    @Mock
    private LessonRepository lessonRepository;
    @Mock
    private QuestionOptionMapper questionOptionMapper;
    @Mock
    private QuestionOptionRepository questionOptionRepository;

    @InjectMocks
    private QuestionBankServiceImpl questionBankService;

    @Test
    void getQuestionByIdShouldThrowWhenNotFound() {
        UUID questionId = UUID.randomUUID();
        when(questionBankRepository.findById(questionId)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> questionBankService.getQuestionById(questionId));

        assertEquals(ErrorCode.QUESTION_BANK_NOT_FOUND, ex.getErrorCode());
    }
}

