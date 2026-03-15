package com.example.unicode.entity;

import com.example.unicode.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "watermark_fingerprint")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class WatermarkFingerprint extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID userId;
    private String userEmail;

    private UUID documentId;
    private String documentTitle;

    private int pageNumber;

    @Column(length = 512)
    private String pageHash;

    private Instant downloadedAt;
}
