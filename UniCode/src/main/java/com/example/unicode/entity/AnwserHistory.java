package com.example.unicode.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
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
    @JoinColumn(name = "question_id")
    private Question question;
    @ManyToOne
    @JoinColumn(name = "examAttempt_id")
    private ExamAttempt examAttempt;

}
