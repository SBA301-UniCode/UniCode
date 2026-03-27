package com.example.unicode.service.impl;

import com.example.unicode.dto.response.ExamAttemptRespone;
import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;
import com.example.unicode.mapper.*;
import com.example.unicode.repository.*;
import com.example.unicode.service.ProcessService;
import com.example.unicode.service.UserService;
import com.example.unicode.ultils.CodeRunnerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExamServiceImplTest {

    @Mock private ExamMapper examMapper;
    @Mock private ContentRepo contentRepo;
    @Mock private ExamRepository examRepository;
    @Mock private QuestionBankRepository questionBankRepository;
    @Mock private QuestionBankMapper questionBankMapper;
    @Mock private ExamAttemptRepository examAttemptRepository;
    @Mock private UsersRepository usersRepository;
    @Mock private QuestionExamRepository questionExamRepository;
    @Mock private QuestionOptionRepository questionOptionRepository;
    @Mock private AnswerHistoryRepository answerHistoryRepository;
    @Mock private ExamAttemptMapper examAttemptMapper;
    @Mock private LessonRepository lessonRepository;
    @Mock private PracticeExamMapper practiceExamMapper;
    @Mock private PracticeSubmissionRepository practiceSubmissionRepository;
    @Mock private PracticeExamRepository practiceExamRepository;
    @Mock private PracticeResultRepository practiceResultRepository;
    @Mock private TestCaseRepository testCaseRepository;
    @Mock private UserService userService;
    @Mock private ProcessService processService;
    @Mock private CodeRunnerService codeRunnerService;

    @InjectMocks
    private ExamServiceImpl examService;

    @Test
    void startExamShouldThrowWhenExamNotFound() {
        UUID examId = UUID.randomUUID();
        when(examRepository.findByExamId(examId)).thenReturn(null);

        AppException ex = assertThrows(AppException.class, () -> examService.startExam(examId));

        assertEquals(ErrorCode.EXAM_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void parseParamTypesShouldParseJsonArray() {
        assertEquals(2, examService.parseParamTypes("[\"int\",\"String\"]").size());
    }
}

