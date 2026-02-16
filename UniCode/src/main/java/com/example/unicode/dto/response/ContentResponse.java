package com.example.unicode.dto.response;

import com.example.unicode.enums.ContentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentResponse {

    private UUID contentId;

    private ContentType contentType;

    private UUID lessonId;


}
