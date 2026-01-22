package com.example.unicode.entity;

import com.example.unicode.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Entity
@Table(name = "QuestionExam")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class QuestionExam extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID questionId;
    @ManyToOne
    @JoinColumn(name = "exam_id")
    private Exam exam;
    @ManyToOne()
    @JoinColumn(name = "question_bank_id")
    private QuestionBank questionBank;
    @OneToMany(mappedBy = "questionExam")
    private List<AnwserHistory> anwserHistorieList = new ArrayList<>();


}
