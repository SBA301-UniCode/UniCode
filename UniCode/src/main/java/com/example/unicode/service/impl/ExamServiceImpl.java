package com.example.unicode.service.impl;

import com.example.unicode.dto.request.ExamRequest;
import com.example.unicode.dto.response.ExamResponse;
import com.example.unicode.dto.response.QuestionBankResponse;
import com.example.unicode.entity.*;
import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;
import com.example.unicode.mapper.ExamMapper;
import com.example.unicode.mapper.QuestionBankMapper;
import com.example.unicode.repository.ContentRepo;
import com.example.unicode.repository.ExamRepository;
import com.example.unicode.repository.QuestionBankRepository;
import com.example.unicode.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {
    private final ExamMapper examMapper;
    private final ContentRepo contentRepo;
    private final ExamRepository examRepository;
    private final QuestionBankRepository questionBankRepository;
    private final QuestionBankMapper questionBankMapper;

    public ExamResponse createExam(UUID contentId, ExamRequest request) {
        Content content = contentRepo.findByContentId(contentId);
        if(content==null){
            throw new AppException(ErrorCode.CONTENT_NOT_FOUND);
        }
        Exam exam = examMapper.toEntity(request);
        exam.setContent(content);
        List<QuestionBank> questionBankList = questionBankRepository.findByLesson_LessonId(content.getLesson().getLessonId());
        Collections.shuffle(questionBankList);
        if (questionBankList.size() < request.getNumberQuestions()) {
            throw new AppException(ErrorCode.INSUFFICIENT_QUESTIONS);
        }
        List<QuestionBank> selectedQuestions = questionBankList.stream()
                .limit(request.getNumberQuestions()).toList();

        List<QuestionExam> questionExamList = selectedQuestions.stream()
                .map(questionBank -> {
                    QuestionExam questionExam = new QuestionExam();
                    questionExam.setExam(exam);
                    questionExam.setQuestionBank(questionBank);
                    return questionExam;
                })
                .toList();

        exam.setQuestionExamList(questionExamList);
        return examMapper.toResponse(examRepository.save(exam));
    }

    public ExamResponse updateExam(UUID examId, ExamRequest request) {
        Exam exam = examRepository.findByExamId(examId);
        if (exam == null) {
            throw new AppException(ErrorCode.EXAM_NOT_FOUND);
        }
        exam = examMapper.toEntity(request);
        return examMapper.toResponse(exam);
    }

    public void changeStatus(UUID examId) {
        Exam exam = examRepository.findByExamId(examId);
        if (exam == null) {
            throw new AppException(ErrorCode.EXAM_NOT_FOUND);
        }
        if (exam.getDeleted()) {
            exam.setDeleted(false);
        } else {
            exam.setDeleted(true);
        }
        examRepository.save(exam);

    }

    @Override
    public List<QuestionBankResponse> getQuestionsByExam(UUID examId) {
        Exam exam = examRepository.findByExamId(examId);
        if (exam == null) {
            throw new AppException(ErrorCode.EXAM_NOT_FOUND);
        }
        List<QuestionBank> questionBanks = exam.getQuestionExamList().stream()
                .map(QuestionExam::getQuestionBank)
                .toList();
        return questionBankMapper.toResponseList(questionBanks);
    }

    @Override
    public ExamResponse getExamById(UUID examId) {
        Exam exam = examRepository.findByExamId(examId);
        if (exam == null) {
            throw new AppException(ErrorCode.EXAM_NOT_FOUND);
        }
        return examMapper.toResponse(exam);
    }


}
