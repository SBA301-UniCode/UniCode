package com.example.unicode.entity;

import com.example.unicode.base.BaseEntity;
import com.example.unicode.enums.VideoStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "Video")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Video extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID videoId;
    private String publicId;
    private int duration;
    @Enumerated(EnumType.STRING)
    private VideoStatus status;
    @OneToOne
    @JoinColumn(name = "content_id")
    private Content content;




}
