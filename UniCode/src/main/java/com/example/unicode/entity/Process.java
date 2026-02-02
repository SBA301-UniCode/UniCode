package com.example.unicode.entity;

import com.example.unicode.base.BaseEntity;
import com.example.unicode.enums.StatusContent;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Process")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Process extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID processId;
    private StatusContent statusContent;
    private LocalDateTime updateDate;
    @ManyToOne
    @JoinColumn(name = "enrollment_id")
    private Enrollment enrollment;
    @ManyToOne
    @JoinColumn(name = "content_id")
    private Content content;
    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;
    @ManyToOne
    @JoinColumn(name = "chapter_id")
    private Chapter chapter;
}
