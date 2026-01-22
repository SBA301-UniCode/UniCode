package com.example.unicode.entity;

import com.example.unicode.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "Roles")
public class Role extends BaseEntity {
    @Id
    private String roleCode;
    private String roleName;
    private String description;
    @ManyToMany
    @JoinTable(
            name = "role_Privilege",
            joinColumns = @JoinColumn(name = "role_code"),
            inverseJoinColumns = @JoinColumn(name = "privilege_code")
    )
    private Set<Privilege> privileges = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "role_code"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<Users> userslist = new HashSet<>();

}
