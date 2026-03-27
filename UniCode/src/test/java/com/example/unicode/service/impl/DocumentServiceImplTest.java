package com.example.unicode.service.impl;

import com.example.unicode.dto.request.DocumentUpdateRequest;
import com.example.unicode.mapper.DocumentMapper;
import com.example.unicode.repository.ContentRepo;
import com.example.unicode.repository.DocumentRepository;
import com.example.unicode.repository.LessonRepository;
import com.example.unicode.ultils.S3Service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentServiceImplTest {

    @Mock
    private DocumentRepository documentRepository;
    @Mock
    private ContentRepo contentRepository;
    @Mock
    private DocumentMapper documentMapper;
    @Mock
    private LessonRepository lessonRepository;
    @Mock
    private com.example.unicode.service.CloudinaryService cloudinaryService;
    @Mock
    private S3Service s3Service;

    @InjectMocks
    private DocumentServiceImpl documentService;

    @Test
    void updateShouldThrowWhenDocumentNotFound() {
        UUID documentId = UUID.randomUUID();
        when(documentRepository.findById(documentId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> documentService.update(documentId, new DocumentUpdateRequest("url", "title")));
    }
}

