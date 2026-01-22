package com.example.unicode.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "Chapter")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AnwserHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID anwserHistoryId;
    private String anwserText;
    @ManyToOne
    @JoinColumn(name = "question_exam_id")
    private QuestionExam questionExam;
    @ManyToOne
    @JoinColumn(name = "examAttempt_id")
    private ExamAttempt examAttempt;

}
