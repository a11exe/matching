package com.example.matching.service;

import com.example.matching.model.MatchingResult;
import com.example.matching.model.Transaction1;
import com.example.matching.model.Transaction2;
import com.example.matching.repository.MatchingResultRepository;
import com.example.matching.repository.Transaction1Repository;
import com.example.matching.repository.Transaction2Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CheckMatchingService {

    static final Logger log = LoggerFactory.getLogger(CheckMatchingService.class);

    private final MatchingResultRepository matchingResultRepository;
    private final Transaction1Repository transaction1Repository;
    private final Transaction2Repository transaction2Repository;
    @Value( "${transactions.count}" )
    private Integer transactionsCount;

    public CheckMatchingService(MatchingResultRepository matchingResultRepository,
                                Transaction1Repository transaction1Repository,
                                Transaction2Repository transaction2Repository) {
        this.matchingResultRepository = matchingResultRepository;
        this.transaction1Repository = transaction1Repository;
        this.transaction2Repository = transaction2Repository;
    }

    public void checkMatchingResult() {
        log.info("Start checking matching results");
        int errors = 0;
        Set<Transaction1> transaction1Set = new HashSet<>();
        List<MatchingResult> matchingResults = matchingResultRepository.findAll();
        int expectedMatchingCount = (transactionsCount / 3) * 2;
        if (expectedMatchingCount == matchingResults.size()) {
            log.info("Expected {} found {} matched results", expectedMatchingCount, matchingResults.size());
        } else {
            errors++;
            log.error("ERROR Expected {} found {} matched results", expectedMatchingCount, matchingResults.size());
        }

        for (MatchingResult matchingResult: matchingResults) {
            Transaction1 transaction1 = matchingResult.getTransaction1();
            Transaction2 transaction2 = matchingResult.getTransaction2();
            if (!transaction1Set.contains(transaction1)) {
                transaction1Set.add(transaction1);
            } else {
                log.error("ERROR transaction 1 id {} matched more than once", transaction1.getId());
                errors++;
            }
            if (transaction1.getUnreconciledAmount().compareTo(BigDecimal.ZERO) != 0) {
                log.error("ERROR transaction 1 id {} has unreconciled amount {}",
                        transaction1.getId(), transaction1.getUnreconciledAmount());
                errors++;
            }
            if (transaction2.getUnreconciledAmount().compareTo(BigDecimal.ZERO) != 0) {
                log.error("ERROR transaction 2 id {} has unreconciled amount {}",
                        transaction2.getId(), transaction2.getUnreconciledAmount());
                errors++;
            }
        }

        List<Transaction1> transaction1List = transaction1Repository.findAll();
        transaction1List.removeAll(matchingResults.stream().map(MatchingResult::getTransaction1).toList());
        if (transaction1List.size() > 0) {
            log.error("Found {} unmatched transactions 1", transaction1List.size());
            for (Transaction1 transaction1: transaction1List) {
                log.error("ERRROR Unmatched transaction 1 id {}", transaction1.getId());
                errors++;
            }
        }

        List<Transaction2> transaction2List = transaction2Repository.findAll();
        transaction2List.removeAll(matchingResults.stream().map(MatchingResult::getTransaction2).toList());
        if (transaction2List.size() > 0) {
            log.error("Found {} unmatched transactions 2", transaction2List.size());
            for (Transaction2 transaction2: transaction2List) {
                log.error("ERRROR Unmatched transaction 2 id {}", transaction2.getId());
                errors++;
            }
        }
        log.info("Total {} errors", errors);
    }
}
