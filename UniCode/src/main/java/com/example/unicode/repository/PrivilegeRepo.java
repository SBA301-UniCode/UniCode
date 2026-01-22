package com.example.unicode.repository;

import com.example.unicode.entity.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PrivilegeRepo extends JpaRepository<Privilege, String> {

    List<Privilege> findAllByDeletedFalse();

    Optional<Privilege> findByPrivilegeCodeAndDeletedFalse(String privilegeCode);

    boolean existsByPrivilegeCodeAndDeletedFalse(String privilegeCode);
}
