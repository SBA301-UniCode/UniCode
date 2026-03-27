package com.example.unicode.repository;

import com.example.unicode.entity.Exam;
import com.example.unicode.entity.ExamAttempt;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, UUID> {
    Optional<ExamAttempt> findByExamAttemptId(UUID examAttemptId);
    List<ExamAttempt> findByLearner_userIdAndExam(UUID learnerUserId, Exam exam, Pageable pageable);

}
