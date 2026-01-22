package com.example.unicode.entity;

import com.example.unicode.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Entity
@Table(name = "RefreshToken")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RefreshToken extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long refreshTokenId;
    private String token;
    private LocalDateTime expiryDate;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;
}
