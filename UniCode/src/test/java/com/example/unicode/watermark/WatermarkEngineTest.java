package com.example.unicode.watermark;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WatermarkEngineTest {

    private final WatermarkEngine engine = new WatermarkEngine();

    @Test
    void embedAndExtractShouldRoundTripPlainText() throws Exception {
        byte[] source = buildSolidPng(160, 160, 0x33, 0x77, 0xAA);

        byte[] watermarked = engine.embed(source, "hello-watermark");
        WatermarkResult result = engine.extract(watermarked);

        assertTrue(result.isFound());
        assertEquals("hello-watermark", result.getText());
    }

    @Test
    void embedMetadataShouldKeepJsonContent() throws Exception {
        byte[] source = buildSolidPng(180, 180, 0x22, 0x22, 0xDD);

        byte[] watermarked = engine.embedMetadata(source, Map.of("userId", "u-1", "email", "u@test.com"));
        WatermarkResult result = engine.extract(watermarked);

        assertTrue(result.isFound());
        assertTrue(result.getText().contains("\"userId\":\"u-1\""));
        assertTrue(result.getText().contains("\"email\":\"u@test.com\""));
    }

    @Test
    void extractAutoShouldReturnNotFoundForPlainImage() throws Exception {
        byte[] source = buildSolidPng(100, 100, 0x10, 0x20, 0x30);

        WatermarkResult result = engine.extractAuto(source, "plain.png");

        assertFalse(result.isFound());
        assertNull(result.getText());
    }

    private byte[] buildSolidPng(int width, int height, int r, int g, int b) throws Exception {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int rgb = (r << 16) | (g << 8) | b;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                image.setRGB(x, y, rgb);
            }
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "png", out);
        return out.toByteArray();
    }
}

