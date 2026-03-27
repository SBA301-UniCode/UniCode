package com.example.unicode;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

class UniCodeApplicationMainTest {

    @Test
    void mainShouldSetTimeZoneAndDelegateToSpringApplication() {
        TimeZone original = TimeZone.getDefault();
        try (MockedStatic<SpringApplication> springApp = mockStatic(SpringApplication.class)) {
            UniCodeApplication.main(new String[0]);

            assertEquals("Asia/Ho_Chi_Minh", TimeZone.getDefault().getID());
            springApp.verify(() -> SpringApplication.run(eq(UniCodeApplication.class), eq(new String[0])));
        } finally {
            TimeZone.setDefault(original);
        }
    }

    @Test
    void constructorShouldBeCallable() {
        UniCodeApplication app = new UniCodeApplication();
        assertEquals(UniCodeApplication.class, app.getClass());
    }
}

