package com.example.unicode.repository;

import com.example.unicode.entity.WatermarkFingerprint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WatermarkFingerprintRepository extends JpaRepository<WatermarkFingerprint, UUID> {
    void deleteByUserIdAndDocumentId(UUID userId, UUID documentId);

    List<WatermarkFingerprint> findByPageHash(String pageHash);
}
