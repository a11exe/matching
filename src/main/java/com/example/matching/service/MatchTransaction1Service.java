package com.example.matching.service;

import com.example.matching.model.Status;
import com.example.matching.model.Transaction1;
import com.example.matching.model.Transaction2;
import com.example.matching.repository.Transaction2Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class MatchTransaction1Service {
    static final Logger log = LoggerFactory.getLogger(MatchTransaction1Service.class);

    private final Transaction2Repository transaction2Repository;
    private final MatchingResultService matchingResultService;

    public MatchTransaction1Service(Transaction2Repository transaction2Repository,
                                    MatchingResultService matchingResultService) {
        this.transaction2Repository = transaction2Repository;
        this.matchingResultService = matchingResultService;
    }

    public void startMatching(Transaction1 transaction1) {
        List<Transaction2> matchCandidates = transaction2Repository.findMatchCandidates(
                transaction1.getUnreconciledAmount(), Arrays.asList(Status.NEW, Status.PARTIAL));
        log.debug("Found {} matching candidates for transaction 1 id {}", matchCandidates.size(), transaction1.getId());
        for (Transaction2 transaction2 : matchCandidates) {
            matchingResultService.match(transaction1, transaction2);
            break;
        }
    }
}
