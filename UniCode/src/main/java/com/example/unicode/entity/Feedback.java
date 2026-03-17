package com.example.unicode.entity;

import com.example.unicode.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "FeedBack")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Feedback extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID feedBackId;
    private String comment;
    private int rating;
    @ManyToOne
    @JoinColumn(name = "learner_id")
    private Users learner;
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
    @OneToMany(mappedBy = "feedback",cascade = CascadeType.ALL,orphanRemoval = true)
    @Builder.Default
    private List<Image> images = new ArrayList<>();
}
