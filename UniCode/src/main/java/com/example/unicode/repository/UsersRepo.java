package com.example.unicode.repository;

import com.example.unicode.entity.Users;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UsersRepo extends JpaRepository<Users, String> {
    Users findByEmail(String email);
}
