package com.example.unicode.service;

import com.example.unicode.dto.request.DocumentCreateRequest;
import com.example.unicode.dto.request.DocumentUpdateRequest;
import com.example.unicode.dto.response.DocumentResponse;

import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface DocumentService {

    DocumentResponse create (DocumentCreateRequest request, MultipartFile file) throws IOException;

     List<DocumentResponse> getAllDocumentByLessonId(UUID lesonId);


    void delete(UUID documentId);

    DocumentResponse update(UUID documentId, DocumentUpdateRequest request);

}


