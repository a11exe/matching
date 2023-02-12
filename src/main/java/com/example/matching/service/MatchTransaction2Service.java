package com.example.matching.service;

import com.example.matching.model.Status;
import com.example.matching.model.Transaction1;
import com.example.matching.model.Transaction2;
import com.example.matching.repository.Transaction1Repository;
import com.example.matching.repository.Transaction2Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service
public class MatchTransaction2Service {
    static final Logger log = LoggerFactory.getLogger(MatchTransaction2Service.class);

    private final Transaction1Repository transaction1Repository;
    private final Transaction2Repository transaction2Repository;
    private final MatchingResultService matchingResultService;

    public MatchTransaction2Service(Transaction1Repository transaction1Repository,
                                    Transaction2Repository transaction2Repository,
                                    MatchingResultService matchingResultService) {
        this.transaction1Repository = transaction1Repository;
        this.transaction2Repository = transaction2Repository;
        this.matchingResultService = matchingResultService;
    }

    public void startMatching(Transaction2 transaction2) {
        List<Transaction1> matchCandidates = transaction1Repository.findMatchCandidates(
                transaction2.getUnreconciledAmount(), List.of(Status.NEW));
        log.debug("Found {} matching candidates for transaction 2 id {}", matchCandidates.size(), transaction2.getId());
        for (Transaction1 transaction1 : matchCandidates) {
            if (transaction2.getUnreconciledAmount().compareTo(transaction1.getUnreconciledAmount()) < 0) {
                continue;
            }
            matchingResultService.match(transaction1, transaction2);
            transaction2 = transaction2Repository.findById(transaction2.getId()).get();
            if (transaction2.getUnreconciledAmount().compareTo(BigDecimal.ZERO) == 0) {
                break;
            }
        }
    }
}
