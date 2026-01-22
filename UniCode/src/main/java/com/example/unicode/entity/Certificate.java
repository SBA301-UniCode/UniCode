package com.example.unicode.entity;

import com.example.unicode.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Certificate")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Certificate extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID certificateId;
    private LocalDateTime certificateDate;
    @ManyToOne
    @JoinColumn(name = "learner_id")
    private Users learner;
    @OneToOne
    @JoinColumn(name = "course_id")
    private Course course;
}
