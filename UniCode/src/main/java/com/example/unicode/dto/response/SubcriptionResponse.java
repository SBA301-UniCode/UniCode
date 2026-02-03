package com.example.unicode.dto.response;

import com.example.unicode.enums.StatusPayment;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SubcriptionResponse {
    private UUID subcriptionId;
    private LocalDateTime subcriptionDate = LocalDateTime.now();
    private Double subcriptionPrice;
    private String message;
    private StatusPayment statusPayment =  StatusPayment.PENDING;
    private UUID courseraId;
    private UUID buyerId;
    private String content;
}
