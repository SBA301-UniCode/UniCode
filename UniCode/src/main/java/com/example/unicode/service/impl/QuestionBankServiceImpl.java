package com.example.unicode.service.impl;

import com.example.unicode.dto.request.QuestionBankRequest;
import com.example.unicode.dto.request.QuestionOptionRequest;
import com.example.unicode.dto.response.PageResponse;
import com.example.unicode.dto.response.QuestionBankResponse;
import com.example.unicode.entity.Lesson;
import com.example.unicode.entity.QuestionBank;
import com.example.unicode.entity.QuestionOption;
import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;
import com.example.unicode.mapper.QuestionBankMapper;
import com.example.unicode.mapper.QuestionOptionMapper;
import com.example.unicode.repository.LessonRepository;
import com.example.unicode.repository.QuestionBankRepository;
import com.example.unicode.repository.QuestionOptionRepository;
import com.example.unicode.service.QuestionBankSerivce;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionBankServiceImpl implements QuestionBankSerivce {
    private final QuestionBankMapper questionBankMapper;
    private final QuestionBankRepository questionBankRepository;
    private final LessonRepository lessonRepository;
    private final QuestionOptionMapper questionOptionMapper;
    private final QuestionOptionRepository questionOptionRepository;


    @Override
    public QuestionBankResponse createQuestion(UUID lessonId, QuestionBankRequest request) {
        QuestionBank questionBank = questionBankMapper.toEntity(request);
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(()-> new AppException(ErrorCode.LESSON_NOT_FOUND));

        questionBank.setLesson(lesson);
        lesson.getQuestionBank().add(questionBank);

        List<QuestionOption> options = request.getOptions().stream()
                .map(optionRequest -> {
                    QuestionOption option = questionOptionMapper.toEntity(optionRequest);
                    option.setQuestionBank(questionBank);
                    return option;
                })
                .toList();

        questionBank.setQuestionOptionList(options);
        QuestionBank savedQuestion = questionBankRepository.save(questionBank);
        return questionBankMapper.toResponse(savedQuestion);
    }

    @Override
    public PageResponse<QuestionBankResponse> getAllQuestionsByLesson(UUID lessonId,int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<QuestionBank> questionPage = questionBankRepository.findByLesson_LessonId(lessonId, pageable);

        return PageResponse.<QuestionBankResponse>builder()
                .content(questionBankMapper.toResponseList(questionPage.getContent()))
                .currentPage(questionPage.getNumber())
                .pageSize(questionPage.getSize())
                .totalElements(questionPage.getTotalElements())
                .totalPages(questionPage.getTotalPages())
                .first(questionPage.isFirst())
                .last(questionPage.isLast())
                .build();
    }

    @Override
    public QuestionBankResponse getQuestionById(UUID questionBankId) {
        QuestionBank questionBank = questionBankRepository.findById(questionBankId)
                .orElseThrow(()-> new AppException(ErrorCode.QUESTION_BANK_NOT_FOUND));
        return questionBankMapper.toResponse(questionBank);
    }

    @Override
    public QuestionBankResponse updateQuestion(UUID questionBankId, QuestionBankRequest request) {
        QuestionBank questionBank = questionBankRepository.findById(questionBankId)
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_BANK_NOT_FOUND));

        // Update các field cơ bản
        questionBank.setQuestionText(request.getQuestionText());
        questionBank.setImageUrl(request.getImageUrl());
        questionBank.setNumberAnswers(request.getNumberAnswers());
        questionBank.setQuestionType(request.getQuestionType());

        // Lấy danh sách option hiện tại
        List<QuestionOption> existingOptions = questionBank.getQuestionOptionList();

        // Map request theo optionId
        Map<UUID, QuestionOptionRequest> requestMap = request.getOptions().stream()
                .filter(opt -> opt.getOptionId() != null) // chỉ lấy những option có ID
                .collect(Collectors.toMap(QuestionOptionRequest::getOptionId, opt -> opt));

        // Update option cũ
        existingOptions.forEach(option -> {
            if (requestMap.containsKey(option.getOptionId())) {
                QuestionOptionRequest optReq = requestMap.get(option.getOptionId());
                option.setAnswerText(optReq.getAnswerText());
                option.setCorrect(optReq.isCorrect());
            }
        });

        // Thêm option mới
        request.getOptions().stream()
                .filter(opt -> opt.getOptionId() == null) // option chưa có ID
                .forEach(optReq -> {
                    QuestionOption newOption = questionOptionMapper.toEntity(optReq);
                    newOption.setQuestionBank(questionBank);
                    existingOptions.add(newOption);
                });

        // Xóa option không còn trong request
        existingOptions.removeIf(opt -> request.getOptions().stream()
                .noneMatch(reqOpt -> reqOpt.getOptionId() != null && reqOpt.getOptionId().equals(opt.getOptionId())));

        // Lưu lại
        QuestionBank updatedQuestion = questionBankRepository.save(questionBank);
        return questionBankMapper.toResponse(updatedQuestion);
    }


    @Override
    public void deleteQuestion(UUID questionBankId) {
        QuestionBank questionBank = questionBankRepository.findById(questionBankId)
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_BANK_NOT_FOUND));
        questionBankRepository.delete(questionBank);

    }


}
