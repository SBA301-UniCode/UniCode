package com.example.unicode.repository;

import com.example.unicode.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepo extends JpaRepository<Role, String> {

    Optional<Role> findByRoleCodeAndDeletedFalse(String roleCode);

    List<Role> findAllByDeletedFalse();

    boolean existsByRoleCodeAndDeletedFalse(String roleCode);
}
