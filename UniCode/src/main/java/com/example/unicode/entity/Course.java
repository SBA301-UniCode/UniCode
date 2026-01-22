package com.example.unicode.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Entity
@Table(name = "Coursera")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID courseId;
    private String title;
    private String description;
    private Double price;
    @ManyToOne
    @JoinColumn(name = "instructor_id")
    private Users instructors;
    @OneToOne
    @JoinColumn(name = "sylabus_id")
    private Sylabus sylabus;
    @OneToMany(mappedBy = "course")
    private Set<RateCondition> rateConditions = new HashSet<>();
    @OneToMany(mappedBy = "course")
    private Set<Feedback> feedbacks = new HashSet<>();
   @OneToOne(mappedBy = "course")
    private Certificate certificate;
   @OneToMany(mappedBy = "course")
    private List<Subcription> subcriptionList = new ArrayList<>();
   @OneToMany(mappedBy = "course")
    private List<Enrollment> enrollmentList = new ArrayList<>();
    @OneToMany(mappedBy = "course")
    private List<Chapter> chapterList = new ArrayList<>();
}
