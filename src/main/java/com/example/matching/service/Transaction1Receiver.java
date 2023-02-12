package com.example.matching.service;

import com.example.matching.model.Status;
import com.example.matching.model.Transaction1;
import com.example.matching.repository.Transaction1Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class Transaction1Receiver {
    private static final Logger log = LoggerFactory.getLogger(Transaction1Receiver.class);

    private final Transaction1Repository transaction1Repository;

    public Transaction1Receiver(Transaction1Repository transaction1Repository) {
        this.transaction1Repository = transaction1Repository;
    }

    public Transaction1 receive() {
        Transaction1 transaction1 = new Transaction1();
        transaction1.setAmount(BigDecimal.valueOf(100));
        transaction1.setUnreconciledAmount(transaction1.getAmount());
        transaction1.setStatus(Status.NEW);
        transaction1Repository.save(transaction1);
        log.debug("Added transaction1 id {} amount {}",
                transaction1.getId(), transaction1.getAmount());
        return transaction1;
    }

}
