package com.example.matching.service;

import com.example.matching.model.Status;
import com.example.matching.model.Transaction2;
import com.example.matching.repository.Transaction2Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class Transaction2Receiver {
    private static final Logger log = LoggerFactory.getLogger(Transaction2Receiver.class);

    private final Transaction2Repository transaction2Repository;

    public Transaction2Receiver(Transaction2Repository transaction2Repository) {
        this.transaction2Repository = transaction2Repository;
    }

    public Transaction2 receive() {
        Transaction2 transaction2 = new Transaction2();
        transaction2.setAmount(BigDecimal.valueOf(200));
        transaction2.setUnreconciledAmount(transaction2.getAmount());
        transaction2.setStatus(Status.NEW);
        transaction2Repository.save(transaction2);
        log.debug("Added transaction2 id {} amount {}",
                transaction2.getId(), transaction2.getAmount());
        return transaction2;
    }

}
