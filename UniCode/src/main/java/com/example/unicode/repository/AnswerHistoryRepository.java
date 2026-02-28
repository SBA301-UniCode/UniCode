package com.example.unicode.repository;

import com.example.unicode.entity.AnwserHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AnswerHistoryRepository extends JpaRepository<AnwserHistory, UUID> {
    List<AnwserHistory> findAllByExamAttempt_ExamAttemptId(UUID examAttemptId);
}
