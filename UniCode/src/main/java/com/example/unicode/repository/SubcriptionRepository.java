package com.example.unicode.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.example.unicode.dto.response.SubcriptionResponse;
import com.example.unicode.entity.Subcription;
import com.example.unicode.enums.StatusPayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.UUID;

public interface SubcriptionRepository extends JpaRepository<Subcription, UUID>, JpaSpecificationExecutor<Subcription> {
    @Query("""
      SELECT SUM(s.subcriptionPrice) from Subcription  s where s.createdAt between :subcriptionDateAfter and :subcriptionDateBefore and s.statusPayment = 'SUCCESS'
""")
    long sumBySubcriptionDate(LocalDateTime subcriptionDateAfter, LocalDateTime subcriptionDateBefore);
    long countByCreatedAtBetween(LocalDateTime createdAtAfter, LocalDateTime createdAtBefore);

    long countByCreatedAtBetweenAndStatusPayment(LocalDateTime createdAtAfter, LocalDateTime createdAtBefore, StatusPayment statusPayment);
}
