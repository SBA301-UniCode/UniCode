package com.example.unicode.entity;

import com.example.unicode.base.BaseEntity;
import com.example.unicode.enums.StatusPayment;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Subcription")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Subcription extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID subcriptionId;
    private Long subcriptionPrice;
    private String message;
    private String content;
    private String payType;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatusPayment statusPayment =  StatusPayment.PENDING;
    @ManyToOne
    @JoinColumn(name = "learner_id")
    private Users learner;
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}


