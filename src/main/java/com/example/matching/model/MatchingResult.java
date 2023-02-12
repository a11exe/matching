package com.example.matching.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class MatchingResult {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "transaction1_id", referencedColumnName = "id")
    private Transaction1 transaction1;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "transaction2_id", referencedColumnName = "id")
    private Transaction2 transaction2;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Transaction1 getTransaction1() {
        return transaction1;
    }

    public void setTransaction1(Transaction1 transaction1) {
        this.transaction1 = transaction1;
    }

    public Transaction2 getTransaction2() {
        return transaction2;
    }

    public void setTransaction2(Transaction2 transaction2) {
        this.transaction2 = transaction2;
    }
}
