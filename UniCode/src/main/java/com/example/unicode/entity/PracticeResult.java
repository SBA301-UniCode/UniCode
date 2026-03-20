package com.example.unicode.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "practice_result")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PracticeResult {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID resultId;

    @ManyToOne
    @JoinColumn(name = "submission_id")
    private PracticeSubmission submission;

    @ManyToOne
    @JoinColumn(name = "test_case_id")
    private TestCase testCase;
    private String actualOutput;
    private String rightAnwser;
    private boolean passed;
}
