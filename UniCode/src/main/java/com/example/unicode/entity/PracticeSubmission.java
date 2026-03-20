package com.example.unicode.entity;

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
@Table(name = "practice_submission")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PracticeSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID submissionId;
    private LocalDateTime submittedAt;
    private String submittedCode;
    private boolean passedAll;

    @ManyToOne
    @JoinColumn(name = "practice_id")
    private PracticeExam practiceExam;

    @ManyToOne
    @JoinColumn(name = "learner_id")
    private Users learner;

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PracticeResult> results = new ArrayList<>();


}
