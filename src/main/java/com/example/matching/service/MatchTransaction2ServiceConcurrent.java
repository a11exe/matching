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
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Service
public class MatchTransaction2ServiceConcurrent {
    static final Logger log = LoggerFactory.getLogger(MatchTransaction2ServiceConcurrent.class);

    private final Transaction1Repository transaction1Repository;
    private final Transaction2Repository transaction2Repository;
    private final MatchingResultService matchingResultService;
    private final MatchingLockService matchingLockService;
    private final ValidationService validationService;

    public MatchTransaction2ServiceConcurrent(Transaction1Repository transaction1Repository,
                                              Transaction2Repository transaction2Repository,
                                              MatchingResultService matchingResultService,
                                              @Qualifier("MatchingLockServiceInMemory")
                                              MatchingLockService matchingLockService,
                                              ValidationService validationService) {
        this.transaction1Repository = transaction1Repository;
        this.transaction2Repository = transaction2Repository;
        this.matchingResultService = matchingResultService;
        this.matchingLockService = matchingLockService;
        this.validationService = validationService;
    }

    public void startMatching(Transaction2 transaction2) {
        Queue<Transaction1> repeatCandidates = new LinkedList<>();

        List<Transaction1> matchCandidates = transaction1Repository.findMatchCandidates(
                transaction2.getUnreconciledAmount(), List.of(Status.NEW));
        log.debug("Found {} matching candidates for transaction 2 id {}", matchCandidates.size(), transaction2.getId());
        for (Transaction1 transaction1 : matchCandidates) {
            if (transaction2.getUnreconciledAmount().compareTo(transaction1.getUnreconciledAmount()) < 0) {
                continue;
            }
            tryMatch(transaction1, transaction2, repeatCandidates);
            transaction2 = transaction2Repository.findById(transaction2.getId()).get();
            if (!isHasUnreconciledAmount(transaction2)) {
                break;
            }
        }
        if (isHasUnreconciledAmount(transaction2)) {
            log.debug("Start repeat matching");
            while (repeatCandidates.size() > 0) {
                Transaction1 transaction1 = repeatCandidates.poll();
                if (validationService.isMatchValid(transaction1, transaction2)) {
                    if (tryMatch(transaction1, transaction2, repeatCandidates)) {
                        break;
                    }
                }
                if (!isHasUnreconciledAmount(transaction2)) {
                    break;
                }
            }
        }
    }

    private boolean tryMatch(Transaction1 transaction1, Transaction2 transaction2, Queue<Transaction1> repeatCandidates) {
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
            repeatCandidates.add(transaction1);
            return false;
        }
    }

    private boolean isHasUnreconciledAmount(Transaction2 transaction2) {
        return transaction2.getUnreconciledAmount().compareTo(BigDecimal.ZERO) > 0;
    }

}
