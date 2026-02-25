package com.example.unicode.entity;

import com.example.unicode.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "AnwserHistory")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AnwserHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID anwserHistoryId;

    @ManyToOne
    @JoinColumn(name = "selected_option_id")
    private QuestionOption selectedOption; // Quan hệ tới option đã chọn

    @ManyToOne
    @JoinColumn(name = "question_exam_id")
    private QuestionExam questionExam;

    @ManyToOne
    @JoinColumn(name = "examAttempt_id")
    private ExamAttempt examAttempt;

}
