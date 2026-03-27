package com.example.unicode.configuration;

import com.example.unicode.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomDecoderTest {

    private static final String TEST_SECRET = "test-secret-key-for-hs512-32-bytes-minimum!!";

    @Mock
    private TokenService tokenService;

    private CustomDecoder decoder;

    @BeforeEach
    void setUp() {
        decoder = new CustomDecoder(tokenService);
        ReflectionTestUtils.setField(decoder, "SECRET_KEY", TEST_SECRET);
    }

    @Test
    void decodeShouldWrapValidationErrors() throws Exception {
        when(tokenService.validateToken("bad")).thenThrow(new RuntimeException("boom"));

        JwtException ex = assertThrows(JwtException.class, () -> decoder.decode("bad"));

        assertEquals("Invalid JWT token", ex.getMessage());
    }

    @Test
    void decodeShouldThrowUnauthorizedWhenTokenInvalid() throws Exception {
        when(tokenService.validateToken("invalid")).thenReturn(false);

        JwtException ex = assertThrows(JwtException.class, () -> decoder.decode("invalid"));

        assertEquals("Unauthorized", ex.getMessage());
    }

    @Test
    void decodeShouldReturnJwtWhenTokenValid() throws Exception {
        String token = buildHs512Jwt(TEST_SECRET);
        when(tokenService.validateToken(token)).thenReturn(true);

        Jwt jwt = decoder.decode(token);

        assertEquals(token, jwt.getTokenValue());
        assertEquals("u", jwt.getSubject());
    }

    private static String buildHs512Jwt(String secret) throws Exception {
        String header = base64Url("{\"alg\":\"HS512\",\"typ\":\"JWT\"}");
        String payload = base64Url("{\"sub\":\"u\",\"exp\":4102444800}");
        String content = header + "." + payload;

        Mac mac = Mac.getInstance("HmacSHA512");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
        String signature = Base64.getUrlEncoder().withoutPadding().encodeToString(
                mac.doFinal(content.getBytes(StandardCharsets.UTF_8))
        );

        return content + "." + signature;
    }

    private static String base64Url(String value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }
}
