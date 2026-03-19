package com.example.unicode.entity;

import com.example.unicode.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "course_mind_map",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "course_id"}))
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CourseMindMap extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String treeData;
}
