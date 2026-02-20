package com.example.unicode.entity;

import com.example.unicode.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Entity
@Table(name = "Exam")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Exam extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID examId;
    private String name;
    private int duration;
    private int numberQuestions;
    private double passScore;
    @OneToOne
    @JoinColumn(name = "content_id")
    private Content content;
    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionExam> questionExamList = new ArrayList<>();
    @OneToMany(mappedBy = "exam")
    private List<ExamAttempt> ExamAttemptList = new ArrayList<>();

}
