package com.example.unicode.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "Users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;
    private String email;
    private String password;
    private String name;
    private int tokenVersion = 0;
    private String avatarUrl;
    private boolean isActive = true;
    private LocalDateTime createAt = LocalDateTime.now();
    @ManyToMany(mappedBy = "userslist" )
    private Set<Role> rolesList = new HashSet<>();
    @OneToMany(mappedBy = "instructors")
    private List<Course> courseList = new ArrayList<>();
    @OneToMany(mappedBy = "learner")
    private List<Feedback> feedbackList = new ArrayList<>();
    @OneToMany(mappedBy = "learner")
    private List<Certificate> certificateList = new ArrayList<>();
    @OneToMany(mappedBy = "learner")
    private List<Subcription> subcriptionsList = new ArrayList<>();
    @OneToMany(mappedBy = "learner")
    private List<Enrollment> enrollmentList = new ArrayList<>();
    @OneToMany(mappedBy = "learner")
    private List<ExamAttempt> examAttemptList = new ArrayList<>();
    @OneToMany(mappedBy = "user")
     private List<RefreshToken> refreshTokens = new ArrayList<>();

}
