package com.example.unicode.repository;

import com.example.unicode.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EnrollmentRepo extends JpaRepository<Enrollment, UUID> {

}
