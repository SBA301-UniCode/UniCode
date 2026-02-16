package com.example.unicode.entity;

import com.example.unicode.base.BaseEntity;
import com.example.unicode.enums.StatusCourse;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "Enrollment")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Enrollment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID enrollmentId;
    @Builder.Default
    private LocalDateTime enrollmentDate = LocalDateTime.now();
    @Builder.Default
    private StatusCourse statusCourse = StatusCourse.IN_PROGRESS;
    @ManyToOne
    @JoinColumn(name = "learner_id")
    private Users learner;
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
    @OneToMany(mappedBy = "enrollment")
    private List<Process> processes = new ArrayList<>();

}
