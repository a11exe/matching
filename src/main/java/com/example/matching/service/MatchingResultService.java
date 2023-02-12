package com.example.matching.service;

import com.example.matching.model.MatchingResult;
import com.example.matching.model.Status;
import com.example.matching.model.Transaction1;
import com.example.matching.model.Transaction2;
import com.example.matching.repository.MatchingResultRepository;
import com.example.matching.repository.Transaction1Repository;
import com.example.matching.repository.Transaction2Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class MatchingResultService {
    static final Logger log = LoggerFactory.getLogger(MatchingResultService.class);

    private final Transaction1Repository transaction1Repository;
    private final Transaction2Repository transaction2Repository;
    private final MatchingResultRepository matchingResultRepository;

    public MatchingResultService(Transaction1Repository transaction1Repository,
                                 Transaction2Repository transaction2Repository,
                                 MatchingResultRepository matchingResultRepository) {
        this.transaction1Repository = transaction1Repository;
        this.transaction2Repository = transaction2Repository;
        this.matchingResultRepository = matchingResultRepository;
    }

    public void match(Transaction1 transaction1, Transaction2 transaction2) {
        transaction1 = transaction1Repository.findById(transaction1.getId()).get();
        transaction2 = transaction2Repository.findById(transaction2.getId()).get();

//        if (transaction1.getUnreconciledAmount().compareTo(BigDecimal.ZERO) > 0
//                && transaction2.getUnreconciledAmount().compareTo(transaction1.getUnreconciledAmount()) >= 0) {

            transaction1.setUnreconciledAmount(BigDecimal.ZERO);
            transaction1.setStatus(Status.COMPLETE);
            transaction1Repository.save(transaction1);

            BigDecimal unreconciledAmount = transaction2.getUnreconciledAmount().subtract(transaction1.getAmount());
            transaction2.setUnreconciledAmount(unreconciledAmount);
            transaction2.setStatus(unreconciledAmount.compareTo(BigDecimal.ZERO) == 0 ? Status.COMPLETE : Status.PARTIAL);
            transaction2Repository.save(transaction2);

            MatchingResult matchingResult = new MatchingResult();
            matchingResult.setTransaction1(transaction1);
            matchingResult.setTransaction2(transaction2);
            matchingResultRepository.save(matchingResult);
            log.debug("Matched transaction 1 id {} with transaction 2 id {}",
                    transaction1.getId(), transaction2.getId());
//        } else {
//            log.error("ERROR can't match wrong amount");
//        }
    }
}


