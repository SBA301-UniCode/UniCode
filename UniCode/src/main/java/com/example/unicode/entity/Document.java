package com.example.unicode.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "Document")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID documentId;
    private String documentUrl;
    private String title;
    @OneToOne
    @JoinColumn(name = "content_id")
    private Content content;
}
