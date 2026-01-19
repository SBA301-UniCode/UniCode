package com.example.unicode.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "Video")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID videoId;
    private String videoUrl;
    private int  duration;
    @OneToOne
    @JoinColumn(name = "content_id")
    private Content content;
}
