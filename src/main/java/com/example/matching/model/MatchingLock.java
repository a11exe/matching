package com.example.matching.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class MatchingLock {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "transaction1_id")
    private UUID transaction1Id;
    @Column(name = "transaction2_id")
    private UUID transaction2Id;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTransaction1Id() {
        return transaction1Id;
    }

    public void setTransaction1Id(UUID transaction1Id) {
        this.transaction1Id = transaction1Id;
    }

    public UUID getTransaction2Id() {
        return transaction2Id;
    }

    public void setTransaction2Id(UUID transaction2Id) {
        this.transaction2Id = transaction2Id;
    }
}
