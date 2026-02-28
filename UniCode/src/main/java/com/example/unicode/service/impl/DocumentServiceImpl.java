//package com.example.unicode.service.impl;
//
//import com.example.unicode.dto.request.DocumentCreateRequest;
//import com.example.unicode.dto.request.DocumentUpdateRequest;
//import com.example.unicode.dto.response.DocumentResponse;
//import com.example.unicode.entity.Content;
//import com.example.unicode.entity.Document;
//import com.example.unicode.mapper.DocumentMapper;
//import com.example.unicode.repository.ContentRepo;
//import com.example.unicode.repository.DocumentRepository;
//import com.example.unicode.service.DocumentService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.UUID;
//
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class DocumentServiceImpl implements DocumentService {
//
//    private final DocumentRepository documentRepository;
//    private final ContentRepo contentRepository;
//    private final DocumentMapper documentMapper;
//
//    @Override
//    public DocumentResponse create(DocumentCreateRequest request) {
//        Content content = contentRepository.findById(request.getContentId()).orElseThrow(() -> new RuntimeException("Content not found with id: " + request.getContentId()));
//
//        Document document = documentMapper.toEntity(request);
//        document.setContent(content);
//        document.setDeleted(false);
//
//        return documentMapper.toResponse(documentRepository.save(document));
//    }
//
//    @Override
//    public List<DocumentResponse> getAllDocumentByLessonId(UUID lesonId) {
//        return List.of();
//    }
//
//    @Override
//    public void delete(UUID lesonId) {
//
//    }
//
//    @Override
//    public DocumentResponse update(UUID documentId, DocumentUpdateRequest request) {
//        return null;
//    }
//}
