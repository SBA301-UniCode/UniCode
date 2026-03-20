package com.example.unicode.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "test_case")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TestCase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID testcaseId;

    private String inputData;
    private String expectedOutput;
    @Enumerated(EnumType.STRING)
    private OutputType outputType;
    private boolean hidden;// phan biet test case hien thi mau va de cham
    private String description; // mô tả test case
    @ManyToOne
    @JoinColumn(name = "practice_id")
    private PracticeExam practiceExam;

    public enum OutputType {
        NUMBER,
        STRING,
        ARRAY
    }
}
