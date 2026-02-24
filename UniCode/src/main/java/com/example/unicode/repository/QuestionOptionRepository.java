package com.example.unicode.repository;

import com.example.unicode.entity.QuestionBank;
import com.example.unicode.entity.QuestionOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface QuestionOptionRepository extends JpaRepository<QuestionOption , UUID> {
    Optional<QuestionOption> findByOptionId(UUID optionId);
    QuestionOption findByQuestionBankAndIsCorrectTrue(QuestionBank request);
}
