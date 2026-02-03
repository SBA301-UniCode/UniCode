package com.example.unicode.repository;

import com.example.unicode.entity.RefreshToken;
import com.example.unicode.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    RefreshToken findByToken(String token);

    Iterable<? extends RefreshToken> findAllByUser(Users user);
}
