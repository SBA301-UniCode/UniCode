package com.example.unicode.repository;

import com.example.unicode.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;



public interface VideoRepository extends JpaRepository<Video, UUID> {

    Optional<Video> findByContent_ContentId(UUID contentId);
    List<Video>findAllByDeletedFalse();


}
