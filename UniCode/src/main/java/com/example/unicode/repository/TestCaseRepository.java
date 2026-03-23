package com.example.unicode.repository;

import com.example.unicode.entity.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TestCaseRepository extends JpaRepository<TestCase, UUID> {
    List<TestCase> findByPracticeExam_PracticeId(UUID practiceId);
}
