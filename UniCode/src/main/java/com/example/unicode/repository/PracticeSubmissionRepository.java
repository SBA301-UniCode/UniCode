package com.example.unicode.repository;

import com.example.unicode.entity.PracticeSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PracticeSubmissionRepository extends JpaRepository<PracticeSubmission, UUID> {
    List<PracticeSubmission> findByLearner_UserId(UUID learnerId);
    List<PracticeSubmission> findByPracticeExam_PracticeId(UUID practiceId);
}
