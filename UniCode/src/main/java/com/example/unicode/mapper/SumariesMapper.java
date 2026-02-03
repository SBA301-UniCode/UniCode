package com.example.unicode.mapper;

import com.example.unicode.dto.response.SubcriptionReportResponse;
import com.example.unicode.entity.Summaries;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SumariesMapper {
    SubcriptionReportResponse.SummariesResponse entityToResponse(Summaries summary);
}
