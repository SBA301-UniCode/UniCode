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
@Table(name = "Lesson")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID lessonId;
    private String title;
    private int orderIndex;
    @ManyToOne
    @JoinColumn(name = "chapter_id")
    private Chapter chapter;
    @OneToMany(mappedBy = "lesson")
    private List<Content> contentList = new ArrayList<>();
    @OneToOne(mappedBy ="lesson")
    private QuestionBank questionBank;
}
