package com.example.unicode.entity;

import com.example.unicode.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ExamAttempt")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ExamAttempt extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID examAttemptId;
    private LocalDateTime attemptStartTime;
    private LocalDateTime attemptEndTime;
    private double score;
    @ManyToOne
    @JoinColumn(name = "learner_id")
    private Users learner;
    @ManyToOne
    @JoinColumn(name = "exam_id")
    private Exam exam;
    @OneToMany(mappedBy = "examAttempt")
    private List<AnwserHistory> AnwserHistoryList = new ArrayList<>();
}
