package com.example.unicode.service.impl;

import com.example.unicode.dto.request.ExamAttemptSubmitRequest;
import com.example.unicode.dto.request.ExamAttemptSubmitRequest.AnswerSubmitRequest;
import com.example.unicode.dto.request.ExamRequest;
import com.example.unicode.dto.response.*;
import com.example.unicode.entity.*;
import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;

import com.example.unicode.mapper.ExamAttemptMapper;
import com.example.unicode.mapper.ExamMapper;
import com.example.unicode.mapper.QuestionBankMapper;
import com.example.unicode.repository.*;
import com.example.unicode.service.ExamService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final ExamAttemptRepository examAttemptRepository;
    private final UsersRepository usersRepository;
    private final QuestionExamRepository questionExamRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final AnswerHistoryRepository answerHistoryRepository;
    private final ExamAttemptMapper examAttemptMapper;

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

    @Override
    public ExamAttemptRespone startExam(UUID examId) {
        Exam exam = examRepository.findByExamId(examId);
        if (exam == null) {
            throw new AppException(ErrorCode.EXAM_NOT_FOUND);
        }
        if (exam.getDeleted()) {
            throw new AppException(ErrorCode.EXAM_INACTIVE);
        }

        ExamAttempt examAttempt = new ExamAttempt();
        examAttempt.setExam(exam);

        examAttempt = examAttemptRepository.save(examAttempt);
        String email = examAttempt.getCreatedBy();
        Users learner = usersRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        examAttempt.setLearner(learner);
        ExamAttempt eA = examAttemptRepository.save(examAttempt);
        ExamAttemptRespone examAttemptRespone = new ExamAttemptRespone();
        examAttemptRespone.setExamAttemptId(eA.getExamAttemptId().toString());
        examAttemptRespone.setExamName(exam.getName());
        examAttemptRespone.setLearnerName(eA.getLearner().getName());

        List<QuestionBankResponse> questions = getQuestionsByExam(exam.getExamId());
        examAttemptRespone.setQuestions(questions);

        return examAttemptRespone;
    }
    public ExamAttempResultsResponse submitExam(ExamAttemptSubmitRequest request){
        ExamAttempt examAttempt = examAttemptRepository.findByExamAttemptId(request.getExamAttemptId())
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_ATTEMPT_NOT_FOUND));
        List<AnwserHistory> anwserHistories = new ArrayList<>();
        List<AnswerSubmitRequest> answers=  request.getAnswers();
        int correctAnswers = 0;
        for(AnswerSubmitRequest answer: answers){
            AnwserHistory anwserHistory = new AnwserHistory();
            anwserHistory.setExamAttempt(examAttempt);
            QuestionExam qE = questionExamRepository.findByExam_ExamIdAndQuestionBank_QuestionBankId(examAttempt.getExam().getExamId(), answer.getQuestionBankId())
                    .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));
            anwserHistory.setQuestionExam(qE);
            QuestionOption option = questionOptionRepository.findByOptionId(answer.getSelectedOptionId())
                    .orElseThrow(() -> new AppException(ErrorCode.ANSWER_NOT_FOUND));
            anwserHistory.setSelectedOption(option);
            if(option.isCorrect()){
                correctAnswers++;
            }
            anwserHistories.add(anwserHistory);
        }
        answerHistoryRepository.saveAll(anwserHistories);
        double score = (double) correctAnswers / examAttempt.getExam().getNumberQuestions() * 10;
        examAttempt.setScore(score);
        if(score>= examAttempt.getExam().getPassScore()) {
            examAttempt.setPassed(true);
        }
        examAttempt.setAttemptEndTime(LocalDateTime.now());
        examAttemptRepository.save(examAttempt);

        return examAttemptMapper.toResultsResponse(examAttempt);
    }
    public List<AnswerHistoryResponse> getExamAttemptHistory(UUID examAttemptId){
        ExamAttempt examAttempt = examAttemptRepository.findByExamAttemptId(examAttemptId)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_ATTEMPT_NOT_FOUND));
        List<AnwserHistory> anwserHistories = answerHistoryRepository.findAllByExamAttempt_ExamAttemptId(examAttemptId);
        if(anwserHistories.isEmpty()){
            throw new AppException(ErrorCode.ANSWER_HISTORY_NOT_FOUND);
        }
        List<AnswerHistoryResponse> answers = new ArrayList<>();
        for(AnwserHistory anwserHistory: anwserHistories){
            AnswerHistoryResponse answer = new AnswerHistoryResponse();
            answer.setQuestionText(anwserHistory.getQuestionExam().getQuestionBank().getQuestionText());
            answer.setSelectedAnswer(anwserHistory.getSelectedOption().getAnswerText());
            answer.setCorrect(anwserHistory.getSelectedOption().isCorrect());
            if(!anwserHistory.getSelectedOption().isCorrect()){
                QuestionOption questionOption = questionOptionRepository.findByQuestionBankAndIsCorrectTrue(anwserHistory.getQuestionExam().getQuestionBank());
                if(questionOption != null){
                    answer.setRightAnswer(questionOption.getAnswerText());
                }
            }
            answers.add(answer);
        }
        return answers;
    }
    public ExamAttempResultsResponse getExamAttemptResults(UUID examAttemptId){
        ExamAttempt examAttempt = examAttemptRepository.findByExamAttemptId(examAttemptId)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_ATTEMPT_NOT_FOUND));
        return examAttemptMapper.toResultsResponse(examAttempt);
    }
}
