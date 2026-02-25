package com.example.unicode.repository;

import com.example.unicode.entity.ExamAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, UUID> {
    Optional<ExamAttempt> findByExamAttemptId(UUID examAttemptId);

}
