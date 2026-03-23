package com.example.unicode.repository;

import com.example.unicode.entity.Course;
import com.example.unicode.entity.Feedback;
import com.example.unicode.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {
    Page<Feedback> findFeedbackByCourse(Course course, Pageable pageable);

    boolean existsByLearnerAndCourse(Users learner, Course course);

    Feedback findByFeedBackId(UUID feedBackId);
}
