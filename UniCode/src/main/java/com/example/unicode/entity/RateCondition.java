package com.example.unicode.entity;

import com.example.unicode.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "rate_condition")
public class RateCondition extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID rate_condition_id;
    private String description;
    private double rate;
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}
