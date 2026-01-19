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
@Table(name = "QuestionBank")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class QuestionBank {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID questionBankId;
    private String questionText;
    private String imageUrl;
    @OneToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;
    @OneToMany(mappedBy = "questionBank")
    private List<Question> questionList = new ArrayList<>();
    @OneToMany(mappedBy = "questionBank")
    private List<QuestionOption> questionOptionList = new ArrayList<>();


}
