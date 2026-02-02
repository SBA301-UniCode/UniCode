package com.example.unicode.repository;

import com.example.unicode.entity.*;
import com.example.unicode.entity.Process;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessRepository extends JpaRepository<Process, UUID> {
    boolean existsProcessByContent_ContentId(UUID contentContentId);

    Process findByContent(Content content);

    Process findByContentAndEnrollment_EnrollmentId(Content content, UUID enrollmentEnrollmentId);

    Process findByLessonAndEnrollment(Lesson lesson, Enrollment enrollment);

    Process findByLessonAndEnrollment_EnrollmentId(Lesson lesson, UUID enrollmentEnrollmentId);

    Process findByChapterAndEnrollment_EnrollmentId(Chapter chapter, UUID enrollmentEnrollmentId);
}
