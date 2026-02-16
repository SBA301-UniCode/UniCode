package com.example.unicode.repository;

import com.example.unicode.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {

    Optional<Course> findByCourseIdAndDeletedFalse(UUID courseId);

    List<Course> findAllByDeletedFalse();

    Page<Course> findAllByDeletedFalse(Pageable pageable);

    boolean existsByTitleAndDeletedFalse(String title);
}

