package com.example.unicode.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentCreateRequest {

    @NotNull(message = "Document URl is required")
    private String documentUrl;
    @NotNull(message = "Document title is required")
    private String title;

    private UUID contentId;

}
