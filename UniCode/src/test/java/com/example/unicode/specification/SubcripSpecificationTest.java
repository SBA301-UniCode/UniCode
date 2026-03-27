package com.example.unicode.specification;

import com.example.unicode.entity.Subcription;
import com.example.unicode.enums.StatusPayment;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class SubcripSpecificationTest {

    @Test
    void constructorShouldBeCallable() {
        assertSame(SubcripSpecification.class, new SubcripSpecification().getClass());
    }

    @Test
    @SuppressWarnings("unchecked")
    void searchByCoursIdShouldReturnNullWhenCourseIdIsNull() {
        Root<Subcription> root = mock(Root.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        var result = SubcripSpecification.searchByCoursId(null).toPredicate(root, null, cb);

        assertNull(result);
        verifyNoInteractions(cb);
    }

    @Test
    @SuppressWarnings("unchecked")
    void searchByCoursIdShouldBuildEqualPredicateWhenCourseIdExists() {
        Root<Subcription> root = mock(Root.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Predicate predicate = mock(Predicate.class);

        Path<Object> coursePath = mock(Path.class);
        Path<Object> courseIdPath = mock(Path.class);
        UUID courseId = UUID.randomUUID();

        when(root.get("course")).thenReturn(coursePath);
        when(coursePath.get("courseId")).thenReturn(courseIdPath);
        when(cb.equal(eq(courseIdPath), eq(courseId))).thenReturn(predicate);

        var result = SubcripSpecification.searchByCoursId(courseId).toPredicate(root, null, cb);

        assertSame(predicate, result);
    }

    @Test
    @SuppressWarnings("unchecked")
    void searchByLernerIdShouldHandleNullAndNonNull() {
        Root<Subcription> root = mock(Root.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Predicate predicate = mock(Predicate.class);

        Path<Object> learnerPath = mock(Path.class);
        Path<Object> learnerIdPath = mock(Path.class);
        UUID learnerId = UUID.randomUUID();

        when(root.get("learner")).thenReturn(learnerPath);
        when(learnerPath.get("userId")).thenReturn(learnerIdPath);
        when(cb.equal(eq(learnerIdPath), eq(learnerId))).thenReturn(predicate);

        assertNull(SubcripSpecification.searchByLernerId(null).toPredicate(root, null, cb));
        assertSame(predicate, SubcripSpecification.searchByLernerId(learnerId).toPredicate(root, null, cb));
    }

    @Test
    @SuppressWarnings("unchecked")
    void searchByStatusPaymentShouldHandleNullAndNonNull() {
        Root<Subcription> root = mock(Root.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Predicate predicate = mock(Predicate.class);

        Path<Object> statusPath = mock(Path.class);
        when(root.get("statusPayment")).thenReturn(statusPath);
        when(cb.equal(eq(statusPath), eq(StatusPayment.SUCCESS))).thenReturn(predicate);

        assertNull(SubcripSpecification.searchByStatusPayment(null).toPredicate(root, null, cb));
        assertSame(predicate, SubcripSpecification.searchByStatusPayment(StatusPayment.SUCCESS).toPredicate(root, null, cb));
    }

    @Test
    @SuppressWarnings("unchecked")
    void searchByDateShouldReturnNullWhenAnyBoundaryIsNull() {
        Root<Subcription> root = mock(Root.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        assertNull(SubcripSpecification.searchByDate(null, LocalDate.now()).toPredicate(root, null, cb));
        assertNull(SubcripSpecification.searchByDate(LocalDate.now(), null).toPredicate(root, null, cb));
        verify(cb, never()).between(any(), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void searchByDateShouldUseStartAndEndOfDayRange() {
        Root<Subcription> root = mock(Root.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Predicate predicate = mock(Predicate.class);

        Path<LocalDateTime> createdAtPath = mock(Path.class);
        LocalDate from = LocalDate.now().minusDays(1);
        LocalDate to = LocalDate.now();

        doReturn(createdAtPath).when(root).<LocalDateTime>get("createdAt");
        when(cb.between(any(), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(predicate);

        var result = SubcripSpecification.searchByDate(from, to).toPredicate(root, null, cb);

        assertSame(predicate, result);
        verify(cb).between(
                eq(createdAtPath),
                eq(from.atStartOfDay()),
                eq(to.plusDays(1).atStartOfDay())
        );
    }
}
