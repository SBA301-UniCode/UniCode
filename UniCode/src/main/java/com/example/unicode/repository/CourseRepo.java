package com.example.unicode.repository;

import com.example.unicode.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CourseRepo extends JpaRepository<Course, UUID> {

    Optional<Course> findByCourseId(UUID courseId);
}

