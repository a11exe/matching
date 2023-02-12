package com.example.matching.repository;

import com.example.matching.model.MatchingLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface MatchingLockRepository extends JpaRepository<MatchingLock, UUID> {

    @Modifying
    @Query("delete from MatchingLock m where m.transaction1Id = :transaction1Id and m.transaction2Id = :transaction2Id")
    void deleteAllByTransactions(
            @Param("transaction1Id") UUID transaction1Id,
            @Param("transaction2Id") UUID transaction2Id);
}
