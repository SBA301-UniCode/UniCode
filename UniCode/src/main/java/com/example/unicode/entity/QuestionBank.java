package com.example.unicode.entity;

import com.example.unicode.base.BaseEntity;
import com.example.unicode.enums.QuestionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "QuestionBank")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class QuestionBank extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID questionBankId;
    private String questionText;
    private String imageUrl;
    private int numberAnswers;
    @Enumerated(EnumType.STRING)
    private QuestionType questionType;
    @OneToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;
    @OneToMany(mappedBy = "questionBank")
    private List<QuestionExam> questionExamList = new ArrayList<>();
    @OneToMany(mappedBy = "questionBank")
    private List<QuestionOption> questionOptionList = new ArrayList<>();


}
