package com.example.unicode.repository;

import com.example.unicode.entity.Privilege;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PrivilegeRepository extends JpaRepository<Privilege, String> {

    List<Privilege> findAllByDeletedFalse();

    Page<Privilege> findAllByDeletedFalse(Pageable pageable);

    Optional<Privilege> findByPrivilegeCodeAndDeletedFalse(String privilegeCode);

    boolean existsByPrivilegeCodeAndDeletedFalse(String privilegeCode);
}
