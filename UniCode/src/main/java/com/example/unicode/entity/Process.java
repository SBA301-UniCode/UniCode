package com.example.unicode.entity;

import com.example.unicode.base.BaseEntity;
import com.example.unicode.enums.StatusContent;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Process")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Process extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID processId;
    private StatusContent statusContent;
    private double percentComplete;
    private LocalDateTime updateDate;
    @ManyToOne
    @JoinColumn(name = "enrollment_id")
    private Enrollment enrollment;
    @ManyToOne
    @JoinColumn(name = "content_id")
    private Content content;
}
