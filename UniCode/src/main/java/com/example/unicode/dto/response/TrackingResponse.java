package com.example.unicode.dto.response;

import com.example.unicode.enums.StatusContent;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;
@Builder
@Data
public class TrackingResponse {

    private double percentComplete;
    private List<ProcessResponse> processResponseList;
 @Builder
    @Data
    public static class ProcessResponse{
     private UUID processId;
     private StatusContent statusContent;
 }

}
