package com.example.unicode.repository;

import com.example.unicode.entity.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PrivilegeRepo extends JpaRepository<Privilege, String> {
}
