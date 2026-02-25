package com.example.unicode.dto.response;


import com.example.unicode.entity.Content;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {

    private UUID documentId;

    private String documentUrl;

    private String title;

    private Content content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
