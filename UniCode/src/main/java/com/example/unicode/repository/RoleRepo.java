package com.example.unicode.repository;

import com.example.unicode.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepo extends JpaRepository<Role, String> {
}
