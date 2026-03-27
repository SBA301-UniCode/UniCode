package com.example.unicode.specification;

import com.example.unicode.entity.Course;
import com.example.unicode.entity.Enrollment;
import com.example.unicode.enums.StatusCourse;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class EnrollmentSpecificationTest {

    @Test
    void constructorShouldBeCallable() {
        assertSame(EnrollmentSpecification.class, new EnrollmentSpecification().getClass());
    }

    @Test
    void searchKeyShouldReturnConjunctionWhenNull() {
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Predicate predicate = mock(Predicate.class);
        when(cb.conjunction()).thenReturn(predicate);
        @SuppressWarnings("unchecked")
        Root<Enrollment> root = mock(Root.class);

        var result = EnrollmentSpecification.searchKey(null).toPredicate(root, null, cb);

        assertSame(predicate, result);
    }

    @Test
    void searchKeyShouldReturnConjunctionWhenBlank() {
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Predicate predicate = mock(Predicate.class);
        when(cb.conjunction()).thenReturn(predicate);
        @SuppressWarnings("unchecked")
        Root<Enrollment> root = mock(Root.class);

        var result = EnrollmentSpecification.searchKey("   ").toPredicate(root, null, cb);

        assertSame(predicate, result);
    }

    @Test
    @SuppressWarnings("unchecked")
    void searchKeyShouldBuildLikePredicatesWhenKeywordPresent() {
        Root<Enrollment> root = mock(Root.class);
        Path<Object> coursePath = mock(Path.class);
        Path<String> titlePath = mock(Path.class);
        Path<String> descriptionPath = mock(Path.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Predicate p1 = mock(Predicate.class);
        Predicate p2 = mock(Predicate.class);
        Predicate out = mock(Predicate.class);

        when(root.get("course")).thenReturn(coursePath);
        when(coursePath.get("title")).thenReturn((Path) titlePath);
        when(coursePath.get("description")).thenReturn((Path) descriptionPath);
        when(cb.lower(titlePath)).thenReturn(titlePath);
        when(cb.lower(descriptionPath)).thenReturn(descriptionPath);
        when(cb.like(eq(titlePath), anyString())).thenReturn(p1);
        when(cb.like(eq(descriptionPath), anyString())).thenReturn(p2);
        when(cb.or(p1, p2)).thenReturn(out);

        var result = EnrollmentSpecification.searchKey("Java").toPredicate(root, null, cb);

        assertSame(out, result);
    }

    @Test
    @SuppressWarnings("unchecked")
    void findByStatusAndIdsShouldHandleNullAndNonNull() {
        Root<Enrollment> root = mock(Root.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Predicate predicate = mock(Predicate.class);

        Path<Object> statusPath = mock(Path.class);
        when(root.get("statusCourse")).thenReturn(statusPath);
        when(cb.equal(eq(statusPath), eq(StatusCourse.COMPLETED))).thenReturn(predicate);

        assertNull(EnrollmentSpecification.findByStatus(null).toPredicate(root, null, cb));
        assertSame(predicate, EnrollmentSpecification.findByStatus(StatusCourse.COMPLETED).toPredicate(root, null, cb));

        Path<Object> coursePath = mock(Path.class);
        Path<Object> courseIdPath = mock(Path.class);
        when(root.get("course")).thenReturn(coursePath);
        when(coursePath.get("courseId")).thenReturn(courseIdPath);

        UUID courseId = UUID.randomUUID();
        when(cb.equal(eq(courseIdPath), eq(courseId))).thenReturn(predicate);

        assertNull(EnrollmentSpecification.findByCourseId(null).toPredicate(root, null, cb));
        assertSame(predicate, EnrollmentSpecification.findByCourseId(courseId).toPredicate(root, null, cb));

        Path<Object> learnerPath = mock(Path.class);
        Path<Object> learnerIdPath = mock(Path.class);
        when(root.get("learner")).thenReturn(learnerPath);
        when(learnerPath.get("userId")).thenReturn(learnerIdPath);

        UUID learnerId = UUID.randomUUID();
        when(cb.equal(eq(learnerIdPath), eq(learnerId))).thenReturn(predicate);

        assertNull(EnrollmentSpecification.findByLearnerId(null).toPredicate(root, null, cb));
        assertSame(predicate, EnrollmentSpecification.findByLearnerId(learnerId).toPredicate(root, null, cb));
    }
}
