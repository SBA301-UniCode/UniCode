package com.example.unicode.schedule;

import com.example.unicode.entity.Summaries;
import com.example.unicode.enums.StatusPayment;
import com.example.unicode.repository.SubcriptionRepository;
import com.example.unicode.repository.SumariesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SumariesSchedule {
    private final SumariesRepository sumariesRepository;
    private final SubcriptionRepository subcriptionRepository;
    @Scheduled(cron = "0 1 0 * * ?")
    @Transactional
    public  void generateReport()
    {
        LocalDateTime localDateTime = LocalDateTime.now();
        Summaries summaries = Summaries.builder()
                .localDate(LocalDate.from(localDateTime))
                .totalPayment(subcriptionRepository.countByCreatedAtBetween(localDateTime.minusDays(1),localDateTime))
                .error(subcriptionRepository.countByCreatedAtBetweenAndStatusPayment(localDateTime.minusDays(1),localDateTime, StatusPayment.ERROR))
                .success(subcriptionRepository.countByCreatedAtBetweenAndStatusPayment(localDateTime.minusDays(1),localDateTime, StatusPayment.SUCCESS))
                .totalAmount(subcriptionRepository.sumBySubcriptionDate(localDateTime.minusDays(1),localDateTime))
            .build();
        sumariesRepository.save(summaries);
    }

}
