package com.example.matching.service;

import com.example.matching.model.Status;
import com.example.matching.model.Transaction1;
import com.example.matching.model.Transaction2;
import com.example.matching.repository.MatchingLockRepository;
import com.example.matching.repository.MatchingResultRepository;
import com.example.matching.repository.Transaction1Repository;
import com.example.matching.repository.Transaction2Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class InitService {

    static final Logger log = LoggerFactory.getLogger(InitService.class);

    private final Transaction1Repository transaction1Repository;
    private final Transaction2Repository transaction2Repository;
    private final MatchingResultRepository matchingResultRepository;
    private final MatchingLockRepository matchingLockRepository;

    public InitService(Transaction1Repository transaction1Repository,
                       Transaction2Repository transaction2Repository,
                       MatchingResultRepository matchingResultRepository,
                       MatchingLockRepository matchingLockRepository) {
        this.transaction1Repository = transaction1Repository;
        this.transaction2Repository = transaction2Repository;
        this.matchingResultRepository = matchingResultRepository;
        this.matchingLockRepository = matchingLockRepository;
    }

    public void clearData() {
        matchingLockRepository.deleteAll();
        matchingResultRepository.deleteAll();
        transaction2Repository.deleteAll();
        transaction1Repository.deleteAll();
    }

}
