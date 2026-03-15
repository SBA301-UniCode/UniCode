package com.example.unicode;

import com.example.unicode.watermark.PdfWatermark;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class PdfWatermarkTest {

    @Test
    public void testEmbedAndExtract() throws Exception {
        System.out.println("Starting PDF Watermark Core Test via JUnit");
        
        // 1. Create a dummy PDF in memory
        byte[] originalPdfBytes = createDummyPdf();
        System.out.println("Dummy PDF generated: " + originalPdfBytes.length + " bytes");

        // 2. Embed Watermark
        String testUserId = UUID.randomUUID().toString();
        String testEmail = "test@example.com";
        System.out.println("Embedding Watermark: userId=" + testUserId + " email=" + testEmail);
        
        byte[] watermarkedBytes = PdfWatermark.embed(originalPdfBytes, testUserId, testEmail);
        System.out.println("Watermarking successful! Size: " + watermarkedBytes.length + " bytes");

        // 3. Extract Watermark
        System.out.println("\nExtracting Watermark...");
        String extracted = PdfWatermark.extract(watermarkedBytes);
        System.out.println("Extracted Result: '" + extracted + "'");
        
        assertNotNull(extracted, "Extracted watermark should not be null");
        assertTrue(extracted.contains("userId"), "Extracted string should contain userId field");
        
        // Parse the JSON and verify fields
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(extracted);
        assertEquals(testUserId, json.get("userId").asText(), "userId should match");
        assertEquals(testEmail, json.get("email").asText(), "email should match");
        assertTrue(json.has("ts"), "Extracted JSON should contain timestamp");
        
        System.out.println("✅ SUCCESS: Extracted watermark JSON correctly contains userId=" + testUserId);
    }
    
    // Helper to generate a minimal valid PDF
    private byte[] createDummyPdf() throws Exception {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        }
    }
}
