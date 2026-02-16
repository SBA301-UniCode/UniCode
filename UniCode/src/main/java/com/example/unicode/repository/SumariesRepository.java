package com.example.unicode.repository;

import com.example.unicode.entity.Summaries;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface SumariesRepository extends JpaRepository<Summaries, UUID> {

    List<Summaries> findByLocalDateBetween(LocalDate localDateAfter, LocalDate localDateBefore);
}
