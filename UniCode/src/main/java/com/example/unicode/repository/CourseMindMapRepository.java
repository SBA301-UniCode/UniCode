package com.example.unicode.repository;

import com.example.unicode.entity.CourseMindMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseMindMapRepository extends JpaRepository<CourseMindMap, UUID> {
    Optional<CourseMindMap> findByUser_UserIdAndCourse_CourseId(UUID userId, UUID courseId);
    void deleteByUser_UserIdAndCourse_CourseId(UUID userId, UUID courseId);
}
