package com.example.unicode.specification;

import com.example.unicode.entity.Enrollment;
import com.example.unicode.enums.StatusCourse;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class EnrollmentSpecification {

    public static Specification<Enrollment> searchKey(String keySearch) {
        return (root, query, cb) -> {
            if (keySearch == null || keySearch.trim().isEmpty()) {
                return cb.conjunction();
            }
            String keyword = "%" + keySearch.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("course").get("title")), keyword),
                    cb.like(cb.lower(root.get("course").get("description")), keyword)
            );
        };
    }
    public  static Specification<Enrollment> findByStatus(StatusCourse statusCourse) {
        return ((root, query, cb) ->
                statusCourse == null ? null : cb.equal(root.get("statusCourse"), statusCourse)
                );
    }
    public static Specification<Enrollment> findByCourseId(UUID courseId) {
        return ((root, query, cb) ->
                courseId == null ? null : cb.equal(root.get("course").get("courseId"), courseId)
        );
    }
    public static Specification<Enrollment> findByLearnerId(UUID learnerId) {
        return ((root, query, cb) ->
                learnerId == null ? null : cb.equal(root.get("learner").get("userId"), learnerId)
        );
    }
}
