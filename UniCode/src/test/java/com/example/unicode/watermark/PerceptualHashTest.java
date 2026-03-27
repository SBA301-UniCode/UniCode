package com.example.unicode.watermark;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.*;

class PerceptualHashTest {

    @Test
    void computeHashShouldReturn16HexChars() throws Exception {
        byte[] imageBytes = buildGradientPng(32, 32);

        String hash = PerceptualHash.computeHash(imageBytes);

        assertNotNull(hash);
        assertEquals(16, hash.length());
    }

    @Test
    void hammingDistanceShouldBeZeroForSameHash() throws Exception {
        byte[] imageBytes = buildGradientPng(24, 24);
        String hash = PerceptualHash.computeHash(imageBytes);

        int distance = PerceptualHash.hammingDistance(hash, hash);

        assertEquals(0, distance);
        assertTrue(PerceptualHash.isMatch(hash, hash));
    }

    @Test
    void hammingDistanceShouldReturn64ForInvalidInput() {
        assertEquals(64, PerceptualHash.hammingDistance(null, "abcd"));
        assertEquals(64, PerceptualHash.hammingDistance("short", "abcd"));
        assertEquals(64, PerceptualHash.hammingDistance("zzzzzzzzzzzzzzzz", "0000000000000000"));
    }

    private byte[] buildGradientPng(int width, int height) throws Exception {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int gray = (x * 255) / Math.max(1, width - 1);
                int rgb = (gray << 16) | (gray << 8) | gray;
                image.setRGB(x, y, rgb);
            }
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "png", out);
        return out.toByteArray();
    }
}

