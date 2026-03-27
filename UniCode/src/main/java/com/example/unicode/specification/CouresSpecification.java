package com.example.unicode.specification;

import com.example.unicode.entity.Course;
import com.example.unicode.entity.Enrollment;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class CouresSpecification {
    public static Specification<Course> searchKey(String keySearch) {
        return (root, query, cb) -> {
            if (keySearch == null || keySearch.trim().isEmpty()) {
                return cb.conjunction();
            }
            String keyword = "%" + keySearch.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("title")), keyword),
                    cb.like(cb.lower(root.get("description")), keyword)
            );
        };
    }
    public static Specification<Course> findByInstructor (UUID instructorId) {
        return ((root, query, cb) ->
                instructorId == null ? null : cb.equal(root.get("instructors").get("userId"), instructorId)
        );
    }
    public static Specification<Course> findbyDeleted(boolean deleted) {
        return  ((root, query, cb) ->

                cb.equal(root.get("deleted"), deleted)
        );
    }
}
