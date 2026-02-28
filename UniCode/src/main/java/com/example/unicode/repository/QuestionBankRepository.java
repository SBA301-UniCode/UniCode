package com.example.unicode.repository;

import com.example.unicode.entity.QuestionBank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuestionBankRepository extends JpaRepository<QuestionBank , UUID> {
    Page<QuestionBank> findByLesson_LessonId(UUID lessonId, Pageable pageable);
    List<QuestionBank> findByLesson_LessonId(UUID lessonId);
}
