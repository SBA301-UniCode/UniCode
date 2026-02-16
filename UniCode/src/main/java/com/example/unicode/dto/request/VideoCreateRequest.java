package com.example.unicode.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoCreateRequest {

        @NotNull(message = "Content ID is required")
        private UUID contentId;

        private int duration;

}

