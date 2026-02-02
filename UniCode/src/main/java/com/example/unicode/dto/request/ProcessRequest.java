package com.example.unicode.dto.request;

import com.example.unicode.enums.StatusContent;
import lombok.Data;

import java.util.UUID;

@Data
public class ProcessRequest {
    private UUID contentId;
    private StatusContent status;
    private UUID enrollmentId;
}
