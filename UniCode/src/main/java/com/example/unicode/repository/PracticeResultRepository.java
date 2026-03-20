package com.example.unicode.repository;

import com.example.unicode.entity.PracticeResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PracticeResultRepository extends JpaRepository<PracticeResult, UUID> {
    List<PracticeResult> findBySubmission_SubmissionId(UUID submissionId);
}
