package com.example.unicode.repository;

import com.example.unicode.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CertificateRepo extends JpaRepository<Certificate, UUID> {

    Optional<Certificate> findByCertificateIdAndDeletedFalse(UUID certificateId);

    List<Certificate> findAllByDeletedFalse();

    List<Certificate> findAllByLearner_UserIdAndDeletedFalse(UUID learnerId);

    boolean existsByCertificateIdAndDeletedFalse(UUID certificateId);

    boolean existsByLearner_UserIdAndCourse_CourseIdAndDeletedFalse(UUID learnerId, UUID courseId);
}

