package com.example.unicode.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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
    @ManyToOne
    @JoinColumn(name ="instructer_id" )
    private Users instructers;
    @OneToMany(mappedBy = "sylabus")
    private List<Courser> courserList = new ArrayList<>();
}
