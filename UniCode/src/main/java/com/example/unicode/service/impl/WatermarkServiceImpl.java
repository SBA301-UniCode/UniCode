package com.example.unicode.service.impl;

import com.example.unicode.dto.response.WatermarkDownloadResult;
import com.example.unicode.dto.response.WatermarkVerifyResponse;
import com.example.unicode.entity.Document;
import com.example.unicode.entity.Users;
import com.example.unicode.entity.WatermarkFingerprint;
import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;
import com.example.unicode.repository.DocumentRepository;
import com.example.unicode.repository.UsersRepository;
import com.example.unicode.repository.WatermarkFingerprintRepository;
import com.example.unicode.service.WatermarkService;
import com.example.unicode.watermark.PdfWatermark;
import com.example.unicode.watermark.PerceptualHash;
import com.example.unicode.watermark.WatermarkEngine;
import com.example.unicode.watermark.WatermarkResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.time.Instant;
import java.util.*;

import com.example.unicode.service.CloudinaryService;

@Slf4j
@Service
@RequiredArgsConstructor
public class WatermarkServiceImpl implements WatermarkService {

    private final WatermarkEngine watermarkEngine;
    private final DocumentRepository documentRepository;
    private final UsersRepository usersRepository;
    private final WatermarkFingerprintRepository fingerprintRepository;
    private final CloudinaryService cloudinaryService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final int CANDIDATE_MATCH_THRESHOLD = 16;
    private static final int CANDIDATE_AMBIGUITY_GAP = 2;
    private static final int GROUP_SCORE_GAP = 14;
    private static final int GROUP_MIN_HITS = 2;
    private static final int HARD_DISTANCE_ACCEPT = 5;
    private static final int MAX_CANDIDATES = 420;

    @Override
    @Transactional
    public WatermarkDownloadResult downloadWithWatermark(UUID documentId) {
        // 1. Get current user
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = usersRepository.findByEmail(email);
        if (user == null) throw new AppException(ErrorCode.USER_NOT_FOUND);

        // 2. Get document
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND));

        // 3. Generate signed URL if it's a Cloudinary URL, then download
        String fileUrl = document.getDocumentUrl();
        String downloadUrl = fileUrl;
        
        if (fileUrl != null && fileUrl.contains("cloudinary.com")) {
            String publicId = document.getPublicId();
            if (publicId != null && !publicId.isEmpty()) {
                // Cloudinary documents use "raw" or "auto" (which becomes "image" or "raw" depending on type)
                // Let's use "image" since that's what we observed in the DB, or fallback to auto
                String resourceType = fileUrl.contains("/raw/upload/") ? "raw" : "image";
                downloadUrl = cloudinaryService.generateSignedDocumentUrl(publicId, resourceType);
                log.info("Generated signed Cloudinary URL for document: {}", documentId);
            }
        }

        byte[] originalBytes;
        try {
            originalBytes = downloadFileFromUrl(downloadUrl);
        } catch (Exception e) {
            log.error("Failed to download document from URL: {}", downloadUrl, e);
            throw new RuntimeException("Failed to download document file: " + e.getMessage());
        }

        // 4. Detect file type from magic bytes & document title
        String fileName = detectFileName(fileUrl, document.getTitle());
        boolean isPdfContent = originalBytes.length >= 5 
                && originalBytes[0] == '%' && originalBytes[1] == 'P' 
                && originalBytes[2] == 'D' && originalBytes[3] == 'F' && originalBytes[4] == '-';
                
        if (isPdfContent && !fileName.toLowerCase().endsWith(".pdf")) {
            fileName = fileName + ".pdf";
        }
        boolean isPdf = isPdfContent || fileName.toLowerCase().endsWith(".pdf");

        // 5. Embed watermark
        byte[] watermarked;
        try {
            watermarked = watermarkEngine.embedAuto(
                    originalBytes, fileName,
                    user.getUserId().toString(), email);
        } catch (Exception e) {
            log.warn("Watermark embedding failed, returning original: {}", e.getMessage(), e);
            watermarked = originalBytes; // Fallback: return original if watermarking fails
        }

        // 6. Store fingerprints for later tracing
        storeFingerprints(watermarked, document, user, isPdf);

        log.info("Document download with watermark: user={} document={}", email, document.getTitle());

        // 7. Build result with correct content type and filename
        String contentType = isPdf ? "application/pdf" : "application/octet-stream";
        String downloadName = buildDownloadName(document.getTitle(), fileName);

        return WatermarkDownloadResult.builder()
                .fileBytes(watermarked)
                .fileName(downloadName)
                .contentType(contentType)
                .build();
    }

    @Override
    public WatermarkVerifyResponse verify(MultipartFile file) {
        try {
            byte[] fileBytes = file.getBytes();
            String fileName = file.getOriginalFilename();
            log.info("Verify: fileName={}, size={}, contentType={}", fileName, fileBytes.length, file.getContentType());

            // Detect PDF by magic bytes (%PDF-) in case filename is wrong
            boolean isPdfContent = fileBytes.length >= 5 
                    && fileBytes[0] == '%' && fileBytes[1] == 'P' 
                    && fileBytes[2] == 'D' && fileBytes[3] == 'F' && fileBytes[4] == '-';
            if (isPdfContent && (fileName == null || !fileName.toLowerCase().endsWith(".pdf"))) {
                log.info("File content is PDF but filename '{}' doesn't have .pdf extension. Correcting.", fileName);
                fileName = (fileName != null ? fileName : "upload") + ".pdf";
            }

            // Strategy 1: Try direct watermark extraction via WatermarkEngine
            log.info("Strategy 1: Trying extractAuto with fileName={}", fileName);
            WatermarkResult result = watermarkEngine.extractAuto(fileBytes, fileName);
            if (result.isFound()) {
                log.info("extractAuto FOUND watermark: {}", result.getText());
                return buildResponseFromExtraction(result, "direct_extraction");
            }
            log.info("Strategy 1 failed: extractAuto did not find watermark");

            // Strategy 1b: If it's a PDF, also try PdfWatermark.extract() directly
            if (isPdfContent) {
                log.info("Strategy 1b: Trying PdfWatermark.extract() directly for PDF content");
                try {
                    String pdfExtracted = PdfWatermark.extract(fileBytes);
                    if (pdfExtracted != null && pdfExtracted.contains("userId")) {
                        log.info("PdfWatermark.extract() FOUND watermark: {}", pdfExtracted);
                        return buildResponseFromExtraction(WatermarkResult.found(pdfExtracted), "pdf_direct_extraction");
                    }
                } catch (Exception e) {
                    log.warn("PdfWatermark.extract() failed: {}", e.getMessage());
                }
            }

            // Strategy 2: Fingerprint matching (works for screenshots)
            log.info("Strategy 2: Trying robust fingerprint matching");
            List<HashCandidate> uploadedCandidates = buildHashCandidates(fileBytes, fileName);
            if (!uploadedCandidates.isEmpty()) {
                List<WatermarkFingerprint> all = fingerprintRepository.findAll();
                if (!all.isEmpty()) {
                    Map<MatchKey, MatchScore> scoreByGroup = new HashMap<>();

                    for (HashCandidate candidate : uploadedCandidates) {
                        WatermarkFingerprint bestFp = null;
                        int bestDistance = Integer.MAX_VALUE;
                        int secondBestDistance = Integer.MAX_VALUE;

                        for (WatermarkFingerprint fp : all) {
                            int dist = PerceptualHash.hammingDistance(candidate.hash(), fp.getPageHash());
                            if (dist < bestDistance) {
                                secondBestDistance = bestDistance;
                                bestDistance = dist;
                                bestFp = fp;
                            } else if (dist < secondBestDistance) {
                                secondBestDistance = dist;
                            }
                        }

                        boolean strictMatch = bestDistance <= PerceptualHash.MATCH_THRESHOLD;
                        boolean candidateMatch = bestDistance <= CANDIDATE_MATCH_THRESHOLD
                                && (bestDistance + CANDIDATE_AMBIGUITY_GAP <= secondBestDistance);

                        if (bestFp != null && (strictMatch || candidateMatch)) {
                            MatchKey key = MatchKey.from(bestFp);
                            MatchScore score = scoreByGroup.computeIfAbsent(key, ignored -> new MatchScore());
                            score.addHit(bestDistance, secondBestDistance, candidate);
                        }
                    }

                    if (!scoreByGroup.isEmpty()) {
                        List<Map.Entry<MatchKey, MatchScore>> ranked = scoreByGroup.entrySet()
                                .stream()
                                .sorted(Comparator
                                        .comparingInt((Map.Entry<MatchKey, MatchScore> e) -> e.getValue().score).reversed()
                                        .thenComparingInt(e -> e.getValue().hits).reversed()
                                        .thenComparingInt(e -> e.getValue().bestDistance))
                                .toList();

                        Map.Entry<MatchKey, MatchScore> top = ranked.get(0);
                        MatchScore topScore = top.getValue();
                        int secondScore = ranked.size() > 1 ? ranked.get(1).getValue().score : 0;

                        boolean passByHits = topScore.hits >= GROUP_MIN_HITS;
                        boolean passByDistance = topScore.bestDistance <= HARD_DISTANCE_ACCEPT;
                        boolean passByGap = topScore.score >= secondScore + GROUP_SCORE_GAP || passByDistance;

                        if (passByGap && (passByHits || passByDistance)) {
                            MatchKey bestMatch = top.getKey();
                            String method = topScore.fullFrameHits > 0 && topScore.fullFrameHits >= (topScore.hits / 2)
                                    ? "fingerprint_matching"
                                    : "fingerprint_matching";
                            
                            int confidence = computeConfidence(topScore);
                            
                            log.info("Fingerprint match! method={} user={} doc={} page={} score={} hits={} bestDistance={}", 
                                    method, bestMatch.userEmail(), bestMatch.documentTitle(), bestMatch.pageNumber(),
                                    topScore.score, topScore.hits, topScore.bestDistance);

                            Instant matchedDownloadedAt = all.stream()
                                    .filter(f -> f.getDocumentId().equals(bestMatch.documentId()) && f.getUserId().equals(bestMatch.userId()))
                                    .map(WatermarkFingerprint::getDownloadedAt)
                                    .findFirst().orElse(null);

                            return WatermarkVerifyResponse.builder()
                                    .found(true)
                                    .method(method)
                                    .confidence(confidence / 100.0)
                                    .matchedDocumentId(bestMatch.documentId())
                                    .matchedDocumentTitle(bestMatch.documentTitle())
                                    .matchedUserEmail(bestMatch.userEmail())
                                    .matchedDownloadedAt(matchedDownloadedAt)
                                    .build();
                        } else {
                            log.info("Fingerprint rejected: topScore={} secondScore={} hits={} bestDistance={}",
                                    topScore.score, secondScore, topScore.hits, topScore.bestDistance);
                        }
                    }
                }
            }

            // Not found
            log.info("No watermark or fingerprint match found");
            return WatermarkVerifyResponse.builder()
                    .found(false)
                    .method("none")
                    .confidence(0)
                    .build();

        } catch (IOException e) {
            log.error("Verify failed: {}", e.getMessage(), e);
            return WatermarkVerifyResponse.builder()
                    .found(false)
                    .method("error")
                    .confidence(0)
                    .build();
        }
    }

    // ═══════════════════ Private Helpers ═══════════════════

    private int computeConfidence(MatchScore score) {
        double distanceConfidence = (64 - score.bestDistance) * 100.0 / 64.0;
        double hitBonus = Math.min(22.0, score.hits * 3.5);
        int combined = (int) Math.round(distanceConfidence * 0.78 + hitBonus);
        return Math.max(55, Math.min(99, combined));
    }

    private List<HashCandidate> buildHashCandidates(byte[] fileBytes, String fileName) {
        Map<String, HashCandidate> uniqueCandidates = new LinkedHashMap<>();

        try {
            if (fileName != null && fileName.toLowerCase().endsWith(".pdf")) {
                try (org.apache.pdfbox.pdmodel.PDDocument doc = org.apache.pdfbox.Loader.loadPDF(fileBytes)) {
                    org.apache.pdfbox.rendering.PDFRenderer renderer = new org.apache.pdfbox.rendering.PDFRenderer(doc);
                    int pages = Math.min(doc.getNumberOfPages(), 3);
                    for (int i = 0; i < pages; i++) {
                        BufferedImage page = renderer.renderImageWithDPI(i, 150f);
                        addCandidates(uniqueCandidates, buildImageCandidates(page, "pdf-page-" + i, true));
                    }
                }
            } else {
                BufferedImage img = ImageIO.read(new ByteArrayInputStream(fileBytes));
                if (img == null) return List.of();
                addCandidates(uniqueCandidates, buildImageCandidates(img, "upload", true));
            }
        } catch (Exception e) {
            log.warn("Failed to build hash candidates: {}", e.getMessage());
        }

        return uniqueCandidates.values().stream().limit(MAX_CANDIDATES).toList();
    }

    private List<HashCandidate> buildImageCandidates(BufferedImage source, String labelPrefix, boolean includeSubregions) {
        List<HashCandidate> candidates = new ArrayList<>();
        if (source == null || source.getWidth() < 32 || source.getHeight() < 32) return candidates;

        String fullHash = PerceptualHash.computeHash(source);
        if (fullHash != null) {
            candidates.add(new HashCandidate(fullHash, labelPrefix + ":full", true));
        }

        if (!includeSubregions) return candidates;

        int width = source.getWidth();
        int height = source.getHeight();

        double[] areaScales = { 0.90, 0.75, 0.60, 0.45, 0.32, 0.22, 0.15 };
        double[] aspectRatios = { 0.56, 0.70, 0.90, 1.00, 1.33, 1.78 };
        double[] anchors = { 0.0, 0.5, 1.0 };

        for (double scale : areaScales) {
            double targetArea = width * (double) height * scale;
            for (double ratio : aspectRatios) {
                int candidateW = (int) Math.round(Math.sqrt(targetArea * ratio));
                int candidateH = (int) Math.round(candidateW / ratio);

                if (candidateW < 40 || candidateH < 40 || candidateW > width || candidateH > height) {
                    continue;
                }

                for (double ax : anchors) {
                    for (double ay : anchors) {
                        int x = (int) Math.round((width - candidateW) * ax);
                        int y = (int) Math.round((height - candidateH) * ay);

                        BufferedImage sub = source.getSubimage(x, y, candidateW, candidateH);
                        String hash = PerceptualHash.computeHash(sub);
                        if (hash == null) continue;

                        String cropLabel = String.format("%s:crop[s=%.2f,r=%.2f,ax=%.1f,ay=%.1f]", labelPrefix, scale, ratio, ax, ay);
                        candidates.add(new HashCandidate(hash, cropLabel, false));
                    }
                }
            }
        }

        int minDim = Math.min(width, height);
        int[] tileSizes = {
                Math.max(42, (int) (minDim * 0.16)),
                Math.max(56, (int) (minDim * 0.24)),
                Math.max(72, (int) (minDim * 0.32))
        };

        for (int tileSize : tileSizes) {
            if (tileSize > width || tileSize > height) continue;
            int step = Math.max(14, tileSize / 2);
            for (int y = 0; y <= height - tileSize; y += step) {
                for (int x = 0; x <= width - tileSize; x += step) {
                    BufferedImage sub = source.getSubimage(x, y, tileSize, tileSize);
                    String hash = PerceptualHash.computeHash(sub);
                    if (hash == null) continue;
                    String tileLabel = String.format("%s:tile[size=%d,x=%d,y=%d]", labelPrefix, tileSize, x, y);
                    candidates.add(new HashCandidate(hash, tileLabel, false));
                }
            }
        }

        return candidates;
    }

    private void addCandidates(Map<String, HashCandidate> uniqueCandidates, List<HashCandidate> candidates) {
        for (HashCandidate candidate : candidates) {
            uniqueCandidates.putIfAbsent(candidate.hash(), candidate);
        }
    }

    /**
     * Download file bytes from a URL using HttpURLConnection.
     * Handles redirects, sets User-Agent, and has timeouts.
     */
    private byte[] downloadFileFromUrl(String urlString) throws IOException {
        try {
            HttpURLConnection conn = (HttpURLConnection) URI.create(urlString).toURL().openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setConnectTimeout(15_000);
            conn.setReadTimeout(30_000);
            conn.setInstanceFollowRedirects(true);

            int status = conn.getResponseCode();

            // Handle redirects manually (some 3xx codes are not auto-followed)
            if (status == HttpURLConnection.HTTP_MOVED_TEMP
                    || status == HttpURLConnection.HTTP_MOVED_PERM
                    || status == HttpURLConnection.HTTP_SEE_OTHER
                    || status == 307 || status == 308) {
                String newUrl = conn.getHeaderField("Location");
                conn.disconnect();
                if (newUrl != null) {
                    conn = (HttpURLConnection) URI.create(newUrl).toURL().openConnection();
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                    conn.setConnectTimeout(15_000);
                    conn.setReadTimeout(30_000);
                    status = conn.getResponseCode();
                }
            }

            if (status != HttpURLConnection.HTTP_OK) {
                conn.disconnect();
                throw new IOException("HTTP " + status + " when downloading: " + urlString);
            }

            try (InputStream in = conn.getInputStream()) {
                return in.readAllBytes();
            } finally {
                conn.disconnect();
            }
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("Failed to download from URL: " + urlString, e);
        }
    }

    /**
     * Detect a proper filename with extension.
     * Cloudinary URLs like .../documents/abc123 don't have extensions,
     * so we also check the document title for a recognizable extension.
     */
    private String detectFileName(String url, String title) {
        // Try from URL first
        String fromUrl = extractFileNameFromUrl(url);
        if (hasKnownExtension(fromUrl)) {
            return fromUrl;
        }
        // Try from document title
        if (title != null && hasKnownExtension(title)) {
            return title;
        }
        // If title hints at PDF (contains "pdf" case-insensitive)
        if (title != null && title.toLowerCase().contains("pdf")) {
            return title + ".pdf";
        }
        // Default: treat as image (the watermark engine has a fallback)
        return fromUrl.isEmpty() ? "document.png" : fromUrl;
    }

    private String extractFileNameFromUrl(String url) {
        if (url == null) return "file.png";
        String path = url.contains("?") ? url.substring(0, url.indexOf('?')) : url;
        int lastSlash = path.lastIndexOf('/');
        return lastSlash >= 0 ? path.substring(lastSlash + 1) : path;
    }

    private boolean hasKnownExtension(String name) {
        if (name == null) return false;
        String lower = name.toLowerCase();
        return lower.endsWith(".pdf") || lower.endsWith(".png") || lower.endsWith(".jpg")
                || lower.endsWith(".jpeg") || lower.endsWith(".bmp") || lower.endsWith(".gif")
                || lower.endsWith(".webp") || lower.endsWith(".tiff") || lower.endsWith(".doc")
                || lower.endsWith(".docx") || lower.endsWith(".ppt") || lower.endsWith(".pptx");
    }

    /**
     * Build a user-friendly download filename from the document title.
     */
    private String buildDownloadName(String title, String detectedFileName) {
        if (title == null || title.isBlank()) {
            return detectedFileName;
        }
        // If title already has an extension, use it directly
        if (hasKnownExtension(title)) {
            return title;
        }
        // Append extension from detected filename
        int dot = detectedFileName.lastIndexOf('.');
        String ext = dot >= 0 ? detectedFileName.substring(dot) : ".pdf";
        return title + ext;
    }

    private void storeFingerprints(byte[] fileBytes, Document document, Users user, boolean isPdf) {
        try {
            // Clear old fingerprints for this user+document
            fingerprintRepository.deleteByUserIdAndDocumentId(user.getUserId(), document.getDocumentId());

            if (isPdf) {
                // Render the first page of the PDF to create a perceptual hash for screenshot matching
                try (org.apache.pdfbox.pdmodel.PDDocument doc = org.apache.pdfbox.Loader.loadPDF(fileBytes)) {
                    if (doc.getNumberOfPages() > 0) {
                        org.apache.pdfbox.rendering.PDFRenderer renderer = new org.apache.pdfbox.rendering.PDFRenderer(doc);
                        BufferedImage img = renderer.renderImageWithDPI(0, 150f);
                        String hash = PerceptualHash.computeHash(img);
                        if (hash != null) {
                            fingerprintRepository.save(WatermarkFingerprint.builder()
                                    .userId(user.getUserId())
                                    .userEmail(user.getEmail())
                                    .documentId(document.getDocumentId())
                                    .documentTitle(document.getTitle())
                                    .pageNumber(1)
                                    .pageHash(hash)
                                    .downloadedAt(Instant.now())
                                    .build());
                            log.info("PDF fingerprint stored for page 1");
                        }
                    }
                } catch (Exception e) {
                    log.warn("Failed to generate PDF fingerprint: {}", e.getMessage());
                }
            } else {
                // Image: single perceptual hash
                BufferedImage img = ImageIO.read(new ByteArrayInputStream(fileBytes));
                if (img != null) {
                    String hash = PerceptualHash.computeHash(img);
                    if (hash != null) {
                        fingerprintRepository.save(WatermarkFingerprint.builder()
                                .userId(user.getUserId())
                                .userEmail(user.getEmail())
                                .documentId(document.getDocumentId())
                                .documentTitle(document.getTitle())
                                .pageNumber(0)
                                .pageHash(hash)
                                .downloadedAt(Instant.now())
                                .build());
                        log.info("Image fingerprint stored");
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Failed to store fingerprints (non-fatal): {}", e.getMessage());
        }
    }

    private WatermarkVerifyResponse buildResponseFromExtraction(WatermarkResult result, String method) {
        WatermarkVerifyResponse.WatermarkVerifyResponseBuilder builder = WatermarkVerifyResponse.builder()
                .found(true)
                .method(method)
                .confidence(1.0);

        try {
            JsonNode json = objectMapper.readTree(result.getText());
            if (json.has("userId")) builder.userId(json.get("userId").asText());
            if (json.has("email")) builder.email(json.get("email").asText());
            if (json.has("ts")) builder.timestamp(json.get("ts").asText());
        } catch (Exception e) {
            // Raw text, not JSON
            builder.userId(result.getText());
        }

        return builder.build();
    }

    private record HashCandidate(String hash, String label, boolean fullFrame) {}

    private record MatchKey(UUID userId, String userEmail, UUID documentId, String documentTitle, int pageNumber) {
        private static MatchKey from(WatermarkFingerprint fp) {
            return new MatchKey(fp.getUserId(), fp.getUserEmail(), fp.getDocumentId(), fp.getDocumentTitle(), fp.getPageNumber());
        }
    }

    private static class MatchScore {
        private int score;
        private int hits;
        private int fullFrameHits;
        private int bestDistance = Integer.MAX_VALUE;
        private String bestSource = "n/a";

        private void addHit(int distance, int secondBestDistance, HashCandidate candidate) {
            this.hits++;
            this.score += (65 - distance);
            if (!candidate.fullFrame()) {
                this.score += 4;
            } else {
                this.fullFrameHits++;
            }

            int separation = Math.max(0, secondBestDistance - distance);
            this.score += Math.min(8, separation);

            if (distance < this.bestDistance) {
                this.bestDistance = distance;
                this.bestSource = candidate.label();
            }
        }
    }
}
