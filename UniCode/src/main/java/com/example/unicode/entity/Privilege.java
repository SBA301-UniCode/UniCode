package com.example.unicode.entity;

import com.example.unicode.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Privilege")
@NoArgsConstructor
@Getter
@Setter
public class Privilege extends BaseEntity {
    @Id
    private String privilegeCode;
    private String privilegeName;
    private String description;

    public Privilege(String privilegeCode, String privilegeName, String description) {
        this.privilegeCode = privilegeCode;
        this.privilegeName = privilegeName;
        this.description = description;
    }

    @ManyToMany(mappedBy = "privileges")
    private Set<Role> roles = new HashSet<>();
}
