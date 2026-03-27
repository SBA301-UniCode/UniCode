package com.example.unicode.configuration;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JwtQueryParamFilterTest {

    @Test
    void shouldInjectAuthorizationHeaderForVideoStreamPath() throws Exception {
        JwtQueryParamFilter filter = new JwtQueryParamFilter();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/videos/1/stream");
        request.setParameter("token", "abc");
        MockHttpServletResponse response = new MockHttpServletResponse();

        final String[] authHeader = new String[1];
        FilterChain chain = (req, res) -> authHeader[0] = ((jakarta.servlet.http.HttpServletRequest) req).getHeader("Authorization");

        filter.doFilter(request, response, chain);

        assertEquals("Bearer abc", authHeader[0]);
    }

    @Test
    void shouldPassThroughForOtherPath() throws Exception {
        JwtQueryParamFilter filter = new JwtQueryParamFilter();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/users");
        MockHttpServletResponse response = new MockHttpServletResponse();

        final Object[] seenRequest = new Object[1];
        FilterChain chain = (req, res) -> seenRequest[0] = req;

        filter.doFilter(request, response, chain);

        assertNotNull(seenRequest[0]);
    }
}

