package com.example.unicode.entity;

import com.example.unicode.base.BaseEntity;
import com.example.unicode.enums.ContentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "Content")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Content extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID contentId;
    @Enumerated(EnumType.STRING)
    private ContentType contentType; // e.g., "video", "text", "quiz"
    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;
    @OneToOne(mappedBy = "content")
    private Video video;
    @OneToOne(mappedBy = "content")
    private Document document;
    @OneToOne(mappedBy = "content")
    private Exam exam;
    @OneToOne(mappedBy = "content")
    private PracticeExam practiceExam;
    @OneToMany(mappedBy = "content")
    private List<Process> processList = new ArrayList<>();


}

