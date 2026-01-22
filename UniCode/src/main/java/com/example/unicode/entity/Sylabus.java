package com.example.unicode.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "Sylabus")
public class Sylabus {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String sylabusId;
    private String courseContent;
    private String method;
    private String referenceMaterial;
    @OneToOne(mappedBy = "sylabus")
    private Course course ;
}
