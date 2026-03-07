package com.example.unicode.mapper;

import com.example.unicode.entity.Document;
import com.example.unicode.dto.request.DocumentCreateRequest;
import com.example.unicode.dto.response.DocumentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    @Mapping(source = "documentUrl", target = "documentUrl")
    @Mapping(source = "content.contentId", target = "contentId")
    DocumentResponse toResponse(Document document);

    List<DocumentResponse> toResponseList(List<Document> documents);
}