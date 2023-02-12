package com.example.matching.service;

import com.example.matching.model.Status;
import com.example.matching.model.Transaction1;
import com.example.matching.model.Transaction2;
import com.example.matching.repository.Transaction1Repository;
import com.example.matching.repository.Transaction2Repository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;

@Service
public class ValidationService {
    private final Transaction1Repository transaction1Repository;
    private final Transaction2Repository transaction2Repository;

    public ValidationService(Transaction1Repository transaction1Repository,
                             Transaction2Repository transaction2Repository) {
        this.transaction1Repository = transaction1Repository;
        this.transaction2Repository = transaction2Repository;
    }
    public boolean isMatchValid(Transaction1 transaction1, Transaction2 transaction2) {
        transaction1 = transaction1Repository.findById(transaction1.getId()).get();
        transaction2 = transaction2Repository.findById(transaction2.getId()).get();

        boolean amountValid = (transaction1.getUnreconciledAmount().compareTo(BigDecimal.ZERO) > 0
                && transaction2.getUnreconciledAmount().compareTo(transaction1.getUnreconciledAmount()) >= 0);
        boolean statusValid = Status.NEW.equals(transaction1.getStatus())
                && Arrays.asList(Status.NEW, Status.PARTIAL).contains(transaction2.getStatus());

        return amountValid && statusValid;
    }
}
