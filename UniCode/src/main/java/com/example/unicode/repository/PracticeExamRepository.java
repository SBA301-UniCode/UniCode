package com.example.unicode.repository;

import com.example.unicode.entity.PracticeExam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PracticeExamRepository extends JpaRepository<PracticeExam, UUID> {
    Optional<PracticeExam> findByPracticeId(UUID practiceId);
}
