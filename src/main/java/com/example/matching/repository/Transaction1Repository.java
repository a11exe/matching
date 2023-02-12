package com.example.matching.repository;

import com.example.matching.model.Status;
import com.example.matching.model.Transaction1;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface Transaction1Repository extends JpaRepository<Transaction1, UUID> {
    @Query("SELECT t FROM Transaction1 t WHERE t.unreconciledAmount <= :unreconciledAmount and status in (:statuses)")
    List<Transaction1> findMatchCandidates(
            @Param("unreconciledAmount") BigDecimal unreconciledAmount,
            @Param("statuses")List<Status> statuses);
}
