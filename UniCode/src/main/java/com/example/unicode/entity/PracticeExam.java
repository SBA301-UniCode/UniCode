package com.example.unicode.entity;

import com.example.unicode.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "practice_exam")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PracticeExam extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID practiceId;

    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    private CodeLanguage language;
    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;
    private String starterCode;
    private String rightCode;
    private int totalTestCase;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "content_id")
    private Content content;
    @OneToMany(mappedBy = "practiceExam", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestCase> testCaseList = new ArrayList<>();
    @OneToMany(mappedBy = "practiceExam", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PracticeSubmission> submissions = new ArrayList<>();

    public enum CodeLanguage{
        JAVA , PYTHON
    }
    public enum Difficulty{
        EASY , NORMAL , HARD
    }

}
