package com.example.unicode.repository;

import com.example.unicode.entity.Sylabus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Sylabus entity with soft-delete support.
 */
public interface SylabusRepository extends JpaRepository<Sylabus, UUID> {

    Optional<Sylabus> findBySylabusIdAndDeletedFalse(String sylabusId);

    List<Sylabus> findAllByDeletedFalse();

    Page<Sylabus> findAllByDeletedFalse(Pageable pageable);
}
