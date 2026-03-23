package com.example.unicode.repository;

import com.example.unicode.dto.response.ImageResponse;
import com.example.unicode.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ImageRepository extends JpaRepository<Image, UUID> {
}
