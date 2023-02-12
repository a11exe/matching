package com.example.matching.service;

import com.example.matching.model.Status;
import com.example.matching.model.Transaction1;
import com.example.matching.model.Transaction2;
import com.example.matching.repository.Transaction1Repository;
import com.example.matching.repository.Transaction2Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Service
public class MatchTransaction1ServiceConcurrent {
    private static final Logger log = LoggerFactory.getLogger(MatchTransaction1ServiceConcurrent.class);

    private final Transaction2Repository transaction2Repository;
    private final MatchingResultService matchingResultService;
    private final MatchingLockService matchingLockService;
    private final ValidationService validationService;

    public MatchTransaction1ServiceConcurrent(Transaction2Repository transaction2Repository,
                                              MatchingResultService matchingResultService,
                                              @Qualifier("MatchingLockServiceInMemory")
                                              MatchingLockService matchingLockService,
                                              ValidationService validationService) {
        this.transaction2Repository = transaction2Repository;
        this.matchingResultService = matchingResultService;
        this.matchingLockService = matchingLockService;
        this.validationService = validationService;
    }

    public void startMatching(Transaction1 transaction1) {
        Queue<Transaction2> repeatCandidates = new LinkedList<>();

        List<Transaction2> matchCandidates = transaction2Repository.findMatchCandidates(
                transaction1.getUnreconciledAmount(), Arrays.asList(Status.NEW, Status.PARTIAL));
        log.debug("Found {} matching candidates for transaction 1 id {}", matchCandidates.size(), transaction1.getId());
        boolean isMatched = false;
        for (Transaction2 transaction2 : matchCandidates) {
            if (tryMatch(transaction1, transaction2, repeatCandidates)) {
                break;
            }
        }
        if (!isMatched) {
            log.debug("Start repeat matching");
            while (repeatCandidates.size() > 0) {
                Transaction2 transaction2 = repeatCandidates.poll();
                if (validationService.isMatchValid(transaction1, transaction2)) {
                    if (tryMatch(transaction1, transaction2, repeatCandidates)) {
                        break;
                    }
                }
            }
        }
        repeatCandidates.clear();
    }

    private boolean tryMatch(Transaction1 transaction1, Transaction2 transaction2, Queue<Transaction2> repeatCandidates) {
        try {
            matchingLockService.getLock(transaction1.getId(), transaction2.getId());
            // if something changed before lock was acquired
            if (!validationService.isMatchValid(transaction1, transaction2)) {
                matchingLockService.releaseLock(transaction1.getId(), transaction2.getId());
                return false;
            }
            matchingResultService.match(transaction1, transaction2);
            matchingLockService.releaseLock(transaction1.getId(), transaction2.getId());
            return true;
        } catch (Exception e) {
            log.debug("ERROR getting lock");
            repeatCandidates.add(transaction2);
            return false;
        }
    }

}
