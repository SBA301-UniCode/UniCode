package com.example.unicode.entity;

import com.example.unicode.base.BaseEntity;
import com.example.unicode.enums.StatusPayment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Subcription")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Subcription extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID subcriptionId;
    private LocalDateTime dateStart;
    private LocalDateTime dateEnd;
    @Enumerated(EnumType.STRING)
    private StatusPayment statusPayment;
    @ManyToOne
    @JoinColumn(name = "learner_id")
    private Users learner;
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}


