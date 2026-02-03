package com.example.unicode.mapper;

import com.example.unicode.dto.response.TrackingResponse;
import com.example.unicode.entity.Process;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProcessMapper {
    TrackingResponse.ProcessResponse entityToResponse(Process process);
}
