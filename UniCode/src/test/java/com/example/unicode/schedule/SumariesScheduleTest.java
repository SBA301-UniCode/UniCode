package com.example.unicode.schedule;

import com.example.unicode.repository.SubcriptionRepository;
import com.example.unicode.repository.SumariesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SumariesScheduleTest {

    @Mock
    private SumariesRepository sumariesRepository;
    @Mock
    private SubcriptionRepository subcriptionRepository;

    @InjectMocks
    private SumariesSchedule schedule;

    @Test
    void generateReportShouldSaveSummary() {
        when(subcriptionRepository.countByCreatedAtBetween(any(), any())).thenReturn(10L);
        when(subcriptionRepository.countByCreatedAtBetweenAndStatusPayment(any(), any(), any())).thenReturn(2L);
        when(subcriptionRepository.sumBySubcriptionDate(any(), any())).thenReturn(100L);

        schedule.generateReport();

        var captor = ArgumentCaptor.forClass(com.example.unicode.entity.Summaries.class);
        verify(sumariesRepository).save(captor.capture());
        assertNotNull(captor.getValue().getLocalDate());
    }
}
