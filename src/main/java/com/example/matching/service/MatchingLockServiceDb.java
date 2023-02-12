package com.example.matching.service;

import com.example.matching.model.MatchingLock;
import com.example.matching.repository.MatchingLockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service("MatchingLockServiceDb")
@Transactional
public class MatchingLockServiceDb implements MatchingLockService {
    private static final Logger log = LoggerFactory.getLogger(MatchingLockServiceDb.class);

    private final MatchingLockRepository matchingLockRepository;

    public MatchingLockServiceDb(MatchingLockRepository matchingLockRepository) {
        this.matchingLockRepository = matchingLockRepository;
    }

    public void getLock(UUID transaction1Id, UUID transaction2Id) {
        MatchingLock matchingLock = new MatchingLock();
        matchingLock.setTransaction1Id(transaction1Id);
        matchingLock.setTransaction2Id(transaction2Id);
        matchingLockRepository.save(matchingLock);
        log.debug("Set lock transaction 1 id {} transaction 2 id {}",
                transaction1Id, transaction2Id);
    }

    public void releaseLock(UUID transaction1Id, UUID transaction2Id) {
        matchingLockRepository.deleteAllByTransactions(transaction1Id, transaction2Id);
        log.debug("Released lock transaction 1 id {} transaction 2 id {}",
                transaction1Id, transaction2Id);
    }
}
