package com.example.unicode.repository;

import com.example.unicode.entity.Chapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Chapter entity with soft-delete support.
 */
public interface ChapterRepository extends JpaRepository<Chapter, UUID> {

    Optional<Chapter> findByChapterIdAndDeletedFalse(UUID chapterId);

    List<Chapter> findAllByDeletedFalse();

    Page<Chapter> findAllByDeletedFalse(Pageable pageable);

    List<Chapter> findByCourse_CourseIdAndDeletedFalseOrderByOrderIndexAsc(UUID courseId);

    boolean existsByTitleAndCourse_CourseIdAndDeletedFalse(String title, UUID courseId);
}
