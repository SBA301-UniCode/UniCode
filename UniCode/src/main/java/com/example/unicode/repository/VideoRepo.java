package com.example.unicode.repository;

import com.example.unicode.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VideoRepo extends JpaRepository<Video, UUID> {

}
