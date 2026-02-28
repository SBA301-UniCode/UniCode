//package com.example.unicode.mapper;
//
//import com.example.unicode.entity.Document;
//import com.example.unicode.dto.request.DocumentCreateRequest;
//import com.example.unicode.dto.response.DocumentResponse;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//
//@Mapper(componentModel = "spring")
//public interface DocumentMapper {
//    @Mapping(source = "content.contentId", target = "contentId")
//    @Mapping(source = "documentUrl", target = "url")
//    DocumentResponse toResponse(Document document);
//
//    @Mapping(target = "content", ignore = true)
//    Document toEntity(DocumentCreateRequest request);
//}
