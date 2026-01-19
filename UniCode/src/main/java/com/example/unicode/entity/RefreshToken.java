package com.example.unicode.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "RefreshToken")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID refreshTokenId;
    private String token;
    private LocalDateTime expiryDate;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;
}
