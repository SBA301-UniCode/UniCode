package com.example.unicode.entity;

import com.example.unicode.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Chapter")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Chapter extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private java.util.UUID chapterId;
    private String title;
    private int orderIndex;
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
    @OneToMany(mappedBy = "chapter")
    private List<Lesson> lessonList = new ArrayList<>();
    @OneToMany(mappedBy = "chapter")
    private List<Process> processList = new ArrayList<>();
}
