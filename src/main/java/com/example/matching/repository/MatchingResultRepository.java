package com.example.matching.repository;

import com.example.matching.model.MatchingResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MatchingResultRepository extends JpaRepository<MatchingResult, UUID> {
}
