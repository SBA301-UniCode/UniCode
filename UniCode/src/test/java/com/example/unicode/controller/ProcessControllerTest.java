package com.example.unicode.controller;

import com.example.unicode.dto.request.TrackingRequest;
import com.example.unicode.dto.response.TrackingResponse;
import com.example.unicode.service.ProcessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessControllerTest {

    @Mock
    private ProcessService processService;

    @InjectMocks
    private ProcessController controller;

    @Test
    void processCourseShouldDelegate() {
        TrackingRequest request = TrackingRequest.builder().id(UUID.randomUUID()).enrollmentId(UUID.randomUUID()).build();
        when(processService.getProcessOfCourses(request)).thenReturn(TrackingResponse.builder().build());

        var response = controller.processCourse(request);

        assertEquals(200, response.getStatusCode().value());
        verify(processService).getProcessOfCourses(request);
    }
}

