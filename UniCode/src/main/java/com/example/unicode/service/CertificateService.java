package com.example.unicode.service;

import com.example.unicode.dto.request.CertificateCreateRequest;
import com.example.unicode.dto.response.CertificateResponse;
import com.example.unicode.dto.response.PageResponse;

import java.util.List;
import java.util.UUID;

public interface CertificateService {

    CertificateResponse create(CertificateCreateRequest request);

    CertificateResponse getById(UUID certificateId);

    PageResponse<CertificateResponse> getAll(int page, int size);

    List<CertificateResponse> getByLearnerId(UUID learnerId);

    void delete(UUID certificateId);
}

