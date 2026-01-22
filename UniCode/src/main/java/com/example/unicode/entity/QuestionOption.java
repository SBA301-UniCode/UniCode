package com.example.unicode.entity;

import com.example.unicode.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "QuestionOption")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class QuestionOption extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID optionId;
    private String answerText;
    private boolean isCorrect;
    @ManyToOne
    @JoinColumn(name = "question_bank_id")
    private QuestionBank questionBank;
}
