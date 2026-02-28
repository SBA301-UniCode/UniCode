package com.example.unicode.repository;

import com.example.unicode.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ExamRepository extends JpaRepository<Exam, UUID> {
    Exam findByExamId(UUID examId);

}
