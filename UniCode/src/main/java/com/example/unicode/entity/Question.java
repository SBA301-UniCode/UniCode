package com.example.unicode.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Entity
@Table(name = "Question")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID questionId;
    private int score;
    private int numberAnswers;
    @ManyToOne
    @JoinColumn(name = "exam_id")
    private Exam exam;
    @ManyToOne()
    @JoinColumn(name ="question_bank_id")
    private QuestionBank questionBank;
    @OneToMany(mappedBy = "question")
    private List<AnwserHistory> anwserHistorieList =  new ArrayList<>();


}
