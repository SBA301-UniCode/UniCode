package com.example.unicode.repository;

import com.example.unicode.entity.QuestionExam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface QuestionExamRepository extends JpaRepository<QuestionExam, UUID> {
    Optional<QuestionExam> findByExam_ExamIdAndQuestionBank_QuestionBankId(UUID examExamId, UUID questionBankQuestionBankId);
}
