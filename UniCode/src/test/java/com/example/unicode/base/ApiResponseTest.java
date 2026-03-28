package com.example.unicode.base;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    void successFactoriesShouldPopulateFields() {
        ApiResponse<String> r1 = ApiResponse.success("ok", "data");
        ApiResponse<String> r2 = ApiResponse.success("data");
        ApiResponse<String> r3 = ApiResponse.success();

        assertTrue(r1.isSuccess());
        assertEquals(1000, r1.getCode());
        assertEquals("ok", r1.getMessage());
        assertEquals("data", r1.getData());

        assertTrue(r2.isSuccess());
        assertEquals("data", r2.getMessage());
        assertNull(r2.getData());

        assertTrue(r3.isSuccess());
        assertEquals(1000, r3.getCode());
    }

    @Test
    void errorFactoriesShouldPopulateFields() {
        ApiResponse<String> r1 = ApiResponse.error();
        ApiResponse<String> r2 = ApiResponse.error(9999, "bad");
        ApiResponse<String> r3 = ApiResponse.error("oops");

        assertFalse(r1.isSuccess());
        assertEquals(1004, r1.getCode());

        assertFalse(r2.isSuccess());
        assertEquals(9999, r2.getCode());
        assertEquals("bad", r2.getMessage());

        assertFalse(r3.isSuccess());
        assertEquals("oops", r3.getMessage());
    }
}
