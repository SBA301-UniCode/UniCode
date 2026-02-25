package com.example.unicode.service.impl;

import com.example.unicode.dto.request.DocumentCreateRequest;
import com.example.unicode.dto.request.DocumentUpdateRequest;
import com.example.unicode.dto.response.DocumentResponse;
import com.example.unicode.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional
public class DocumentServiceImpl implements DocumentService {

    @Override
    public DocumentResponse create(DocumentCreateRequest request) {
         return null;
    }

    @Override
    public List<DocumentResponse> getAllDocumentByLessonId(UUID lesonId) {
        return List.of();
    }

    @Override
    public void delete(UUID lesonId) {

    }

    @Override
    public DocumentResponse update(UUID documentId, DocumentUpdateRequest request) {
        return null;
    }
}
