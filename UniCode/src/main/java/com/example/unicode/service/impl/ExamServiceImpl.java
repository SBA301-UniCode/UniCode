package com.example.unicode.service.impl;

import com.example.unicode.dto.request.*;
import com.example.unicode.dto.request.ExamAttemptSubmitRequest.AnswerSubmitRequest;
import com.example.unicode.dto.response.*;
import com.example.unicode.entity.*;
import com.example.unicode.enums.ContentType;
import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;

import com.example.unicode.mapper.ExamAttemptMapper;
import com.example.unicode.mapper.ExamMapper;
import com.example.unicode.mapper.PracticeExamMapper;
import com.example.unicode.mapper.QuestionBankMapper;
import com.example.unicode.repository.*;
import com.example.unicode.service.ExamService;

import com.example.unicode.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

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
    private final LessonRepository lessonRepository;
    private final PracticeExamMapper practiceExamMapper;
    private final PracticeSubmissionRepository practiceSubmissionRepository;
    private final PracticeExamRepository practiceExamRepository;
    private final PracticeResultRepository practiceResultRepository;
    private final TestCaseRepository testCaseRepository;
    private final UserService userService;

    @Override
    public ExamResponse createExam(UUID lessonId, ExamRequest request) {
        Lesson lesson = lessonRepository.findByLessonId(lessonId);
        if (lesson == null) {
            throw new AppException(ErrorCode.LESSON_NOT_FOUND);
        }
        Content content = new Content();
        content.setLesson(lesson);
        content.setContentType(ContentType.QUIZ);
        content = contentRepo.save(content);

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

    @Transactional
    @Override
    public PracticeExamResponse createPracticeExam(UUID lessonId, PracticeExamRequest request) {
        Lesson lesson = lessonRepository.findByLessonId(lessonId);
        if (lesson == null) {
            throw new AppException(ErrorCode.LESSON_NOT_FOUND);
        }

        Content content = new Content();
        content.setLesson(lesson);
        content.setContentType(ContentType.PRACTICE);
        PracticeExam exam = practiceExamMapper.toEntity(request);
        exam.setContent(content);
        if (request.getTestCases() != null && !request.getTestCases().isEmpty()) {
            for (TestCaseRequest tcReq : request.getTestCases()) {
                TestCase tc = practiceExamMapper.toEntity(tcReq);
                tc.setPracticeExam(exam);// gắn ngược lại
                exam.getTestCaseList().add(tc); // gắn vào list
            }
        }
        exam.setTotalTestCase(request.getTestCases().size());
        exam = practiceExamRepository.save(exam);
        return practiceExamMapper.toResponse(exam);
    }

    @Override
    public ExamResponse updateExam(UUID examId, ExamRequest request) {
        Exam exam = examRepository.findByExamId(examId);
        if (exam == null) {
            throw new AppException(ErrorCode.EXAM_NOT_FOUND);
        }
        exam.setDuration(request.getDuration());
        exam.setPassScore(request.getPassScore());
        exam.setName(request.getName());
        return examMapper.toResponse(examRepository.save(exam));
    }

    @Override
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

    @Override
    public ExamAttempResultsResponse submitExam(ExamAttemptSubmitRequest request) {
        ExamAttempt examAttempt = examAttemptRepository.findByExamAttemptId(request.getExamAttemptId())
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_ATTEMPT_NOT_FOUND));
        List<AnwserHistory> anwserHistories = new ArrayList<>();
        List<AnswerSubmitRequest> answers = request.getAnswers();
        int correctAnswers = 0;
        for (AnswerSubmitRequest answer : answers) {
            AnwserHistory anwserHistory = new AnwserHistory();
            anwserHistory.setExamAttempt(examAttempt);
            QuestionExam qE = questionExamRepository.findByExam_ExamIdAndQuestionBank_QuestionBankId(examAttempt.getExam().getExamId(), answer.getQuestionBankId())
                    .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));
            anwserHistory.setQuestionExam(qE);
            QuestionOption option = questionOptionRepository.findByOptionId(answer.getSelectedOptionId())
                    .orElseThrow(() -> new AppException(ErrorCode.ANSWER_NOT_FOUND));
            anwserHistory.setSelectedOption(option);
            if (option.isCorrect()) {
                correctAnswers++;
            }
            anwserHistories.add(anwserHistory);
        }
        answerHistoryRepository.saveAll(anwserHistories);
        double score = (double) correctAnswers / examAttempt.getExam().getNumberQuestions() * 10;
        examAttempt.setScore(score);
        if (score >= examAttempt.getExam().getPassScore()) {
            examAttempt.setPassed(true);
        }
        examAttempt.setAttemptEndTime(LocalDateTime.now());
        examAttemptRepository.save(examAttempt);

        return examAttemptMapper.toResultsResponse(examAttempt);
    }

    @Override
    public List<AnswerHistoryResponse> getExamAttemptHistory(UUID examAttemptId) {
        ExamAttempt examAttempt = examAttemptRepository.findByExamAttemptId(examAttemptId)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_ATTEMPT_NOT_FOUND));
        List<AnwserHistory> anwserHistories = answerHistoryRepository.findAllByExamAttempt_ExamAttemptId(examAttemptId);
        if (anwserHistories.isEmpty()) {
            throw new AppException(ErrorCode.ANSWER_HISTORY_NOT_FOUND);
        }
        List<AnswerHistoryResponse> answers = new ArrayList<>();
        for (AnwserHistory anwserHistory : anwserHistories) {
            AnswerHistoryResponse answer = new AnswerHistoryResponse();
            answer.setQuestionText(anwserHistory.getQuestionExam().getQuestionBank().getQuestionText());
            answer.setSelectedAnswer(anwserHistory.getSelectedOption().getAnswerText());
            answer.setCorrect(anwserHistory.getSelectedOption().isCorrect());
            if (!anwserHistory.getSelectedOption().isCorrect()) {
                QuestionOption questionOption = questionOptionRepository.findByQuestionBankAndIsCorrectTrue(anwserHistory.getQuestionExam().getQuestionBank());
                if (questionOption != null) {
                    answer.setRightAnswer(questionOption.getAnswerText());
                }
            }
            answers.add(answer);
        }
        return answers;
    }

    @Override
    public ExamAttempResultsResponse getExamAttemptResults(UUID examAttemptId) {
        ExamAttempt examAttempt = examAttemptRepository.findByExamAttemptId(examAttemptId)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_ATTEMPT_NOT_FOUND));
        return examAttemptMapper.toResultsResponse(examAttempt);
    }

    @Override
    public PracticeExamResponse getPracticeExamById(UUID id) {
        PracticeExam exam = practiceExamRepository.findByPracticeId(id)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));
        return practiceExamMapper.toResponse(exam);
    }

    @Override
    public void deletePracticeExam(UUID id) {
        PracticeExam exam = practiceExamRepository.findByPracticeId(id)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));
        exam.setDeleted(true);
        practiceExamRepository.save(exam);
    }

    @Override
    public PracticeExamResponse updatePracticeExam(UUID id, PracticeExamRequest request) {
        PracticeExam exam = practiceExamRepository.findByPracticeId(id)
                .orElseThrow(() -> new AppException(ErrorCode.EXAM_NOT_FOUND));
        exam.setTitle(request.getTitle());
        exam.setDescription(request.getDescription());
        exam.setDifficulty(request.getDifficulty());
        exam.setLanguage(request.getLanguage());
        exam.setStarterCode(request.getStarterCode());
        exam.setRightCode(request.getRightCode());
        exam.setTotalTestCase(request.getTestCases().size());

        exam.getTestCaseList().clear();
        for (TestCaseRequest tcReq : request.getTestCases()) {
            TestCase tc = new TestCase();
            tc.setInputData(tcReq.getInputData());
            tc.setExpectedOutput(tcReq.getExpectedOutput());
            tc.setOutputType(tcReq.getOutputType());
            tc.setHidden(tcReq.isHidden());
            tc.setDescription(tcReq.getDescription());
            tc.setPracticeExam(exam);
            exam.getTestCaseList().add(tc);
        }
        return practiceExamMapper.toResponse(practiceExamRepository.save(exam));
    }

    @Override
    public PracticeStartResponse startPracticeExam(UUID contentId) {
        Content content = contentRepo.findByContentId(contentId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTENT_NOT_FOUND));
        PracticeExam exam = content.getPracticeExam();
        Users learner = userService.getUsers();
        if (learner == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        PracticeSubmission submission = new PracticeSubmission();
        submission.setPracticeExam(exam);
        submission.setLearner(learner);
        submission = practiceSubmissionRepository.save(submission);

        List<TestCaseResponse> visibleCases = exam.getTestCaseList().stream()
                .filter(tc -> !tc.isHidden())
                .map(practiceExamMapper::toResponse)
                .toList();

        return PracticeStartResponse.builder()
                .starterCode(exam.getStarterCode())
                .visibleTestCases(visibleCases)
                .difficulty(exam.getDifficulty())
                .language(exam.getLanguage())
                .title(exam.getTitle())
                .practiceId(exam.getPracticeId())
                .submissionId(submission.getSubmissionId())
                .description(exam.getDescription())
                .build();

    }

    @Override
    public PracticeResultResponse submitPracticeExam(PracticeSubmitRequest request) {
        PracticeSubmission submission = practiceSubmissionRepository.findById(request.getSubmissionId())
                .orElseThrow(() -> new AppException(ErrorCode.SUBMISSION_NOT_FOUND));

        PracticeExam exam = submission.getPracticeExam();
        submission.setSubmittedCode(request.getLearnerCode());

        int passed = 0;
        int failed = 0;
        List<TestCaseResultResponse> results = new ArrayList<>();

        for (TestCase tc : exam.getTestCaseList()) {
            // chạy code học viên với input
            String actual = "";
            //String actual = codeRunnerService.run(request.getLearnerCode(), tc.getInputData(), exam.getLanguage());

            boolean ok = compareOutput(tc.getOutputType(), actual, tc.getExpectedOutput());

            if (ok) passed++;
            else failed++;
            PracticeResult result = new PracticeResult();
            result.setSubmission(submission);
            result.setTestCase(tc);
            result.setActualOutput(actual);
            result.setRightAnwser(tc.getExpectedOutput());
            result.setPassed(ok);
            practiceResultRepository.save(result);
            results.add(new TestCaseResultResponse(
                    tc.getTestcaseId(),
                    tc.getInputData(),
                    tc.getExpectedOutput(),
                    actual,
                    tc.isHidden(),
                    ok ? "PASS" : "FAIL"
            ));
        }
        submission.setPass(passed);
        submission.setTotalCases(exam.getTotalTestCase());
        submission.setSubmittedAt(LocalDateTime.now());
        practiceSubmissionRepository.save(submission);

        return new PracticeResultResponse(
                submission.getSubmissionId(),
                exam.getPracticeId(),
                passed,
                failed,
                results
        );
    }


    private boolean compareOutput(TestCase.OutputType type, String actual, String expected) {
        switch (type) {
            case NUMBER:
                try {
                    return Integer.parseInt(actual.trim()) == Integer.parseInt(expected.trim());
                } catch (NumberFormatException e) {
                    return false;
                }
            case STRING:
                return actual.trim().equals(expected.trim());
            case ARRAY:
                // chuẩn hóa mảng: bỏ khoảng trắng, bỏ dấu [ ]
                String[] actualArr = actual.replaceAll("\\s+", "")
                        .replaceAll("[\\[\\]]", "") .split(",");
                String[] expectedArr = expected.replaceAll("\\s+", "")
                        .replaceAll("[\\[\\]]", "")
                    .split(",");
                return Arrays.equals(actualArr, expectedArr);
            default:
                return actual.trim().equals(expected.trim());
        }
    }


}
