package com.example.unicode.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.example.unicode.dto.response.EnrolmentResponse;
import com.example.unicode.entity.Course;
import com.example.unicode.entity.Enrollment;
import com.example.unicode.entity.Users;
import com.example.unicode.enums.StatusCourse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID>, JpaSpecificationExecutor<Enrollment> {
    boolean existsByCourseAndLearner(Course course, Users learner);
    Page<Enrollment> findByCourse(Course course, Pageable pageable);

    Page<Enrollment> getByLearner(Users learner, Pageable pageable);

    Page<Enrollment> getByLearnerAndStatusCourse(Users learner, StatusCourse statusCourse, Pageable pageable);
}
