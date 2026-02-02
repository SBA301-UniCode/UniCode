package com.example.unicode.repository;

import com.example.unicode.entity.Lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface LessonRepo extends JpaRepository<Lesson, UUID> {
    Lesson findByLessonId(UUID lessonId);

    Optional<Lesson> findByLessonIdAndDeletedFalse(UUID lessonId);

    List<Lesson> findAllByDeletedFalse();

    Page<Lesson> findAllByDeletedFalse(Pageable pageable);

    List<Lesson> findByChapter_ChapterIdAndDeletedFalseOrderByOrderIndexAsc(UUID chapterId);

    boolean existsByTitleAndChapter_ChapterIdAndDeletedFalse(String title, UUID chapterId);
}
