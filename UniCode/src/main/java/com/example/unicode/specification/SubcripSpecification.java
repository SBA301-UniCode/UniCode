package com.example.unicode.specification;

import com.example.unicode.dto.request.SearchSubcriptionRequest;
import com.example.unicode.entity.Subcription;
import com.example.unicode.enums.StatusPayment;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class SubcripSpecification {
    public static Specification<Subcription> searchByCoursId(UUID courseId) {
        return ((root, query, cb) ->
                courseId == null ? null : cb.equal(root.get("course").get("courseId"), courseId)
                );
    }
    public static Specification<Subcription> searchByLernerId(UUID learnerId) {
        return ((root, query, cb) ->
                learnerId == null ? null : cb.equal(root.get("learner").get("userId"), learnerId)
        );
    }
    public static Specification<Subcription> searchByStatusPayment(StatusPayment statusPayment) {
        return ((root, query, cb) ->
                statusPayment == null ? null : cb.equal(root.get("statusPayment"), statusPayment)
        );
    }
    public static Specification<Subcription> searchByDate(LocalDate from, LocalDate to) {
        return ((root, query, cb) ->
                from == null || to == null ? null : cb.between(root.get("createdAt"),from.atStartOfDay(),to.plusDays(1).atStartOfDay())
        );
    }

}
