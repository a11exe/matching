package com.example.matching.service;

import com.example.matching.repository.MatchingLockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service("MatchingLockServiceInMemory")
@Transactional
public class MatchingLockServiceInMemory implements MatchingLockService {
    private static final Logger log = LoggerFactory.getLogger(MatchingLockServiceInMemory.class);

    private final ConcurrentHashMap<UUID, UUID> matchingLock;

    public MatchingLockServiceInMemory(MatchingLockRepository matchingLockRepository) {
        this.matchingLock = new ConcurrentHashMap<>();
    }

    public void getLock(UUID transaction1Id, UUID transaction2Id) {

        UUID putResult1 = matchingLock.putIfAbsent(transaction1Id, transaction1Id);
        if (putResult1 != null) {
            throw new RuntimeException("Acquiring lock failed");
        }
        UUID putResult2 = matchingLock.putIfAbsent(transaction2Id, transaction2Id);
        if (putResult2 != null) {
            matchingLock.remove(transaction1Id);
            throw new RuntimeException("Acquiring lock failed");
        }
        log.debug("Set lock transaction 1 id {} transaction 2 id {}",
                transaction1Id, transaction2Id);
    }

    public void releaseLock(UUID transaction1Id, UUID transaction2Id) {
        matchingLock.remove(transaction1Id);
        matchingLock.remove(transaction2Id);
        log.debug("Released lock transaction 1 id {} transaction 2 id {}",
                transaction1Id, transaction2Id);
    }
}
