package com.example.unicode.repository;

import com.example.unicode.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsersRepo extends JpaRepository<Users, UUID> {

    Users findByEmail(String email);

    Optional<Users> findByUserIdAndDeletedFalse(UUID userId);

    Optional<Users> findByEmailAndDeletedFalse(String email);

    List<Users> findAllByDeletedFalse();

    Page<Users> findAllByDeletedFalse(Pageable pageable);

    boolean existsByEmailAndDeletedFalse(String email);

    boolean existsByUserIdAndDeletedFalse(UUID userId);
}
