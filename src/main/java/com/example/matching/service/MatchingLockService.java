package com.example.matching.service;

import java.util.UUID;

public interface MatchingLockService {

    void getLock(UUID transaction1Id, UUID transaction2Id);

    void releaseLock(UUID transaction1Id, UUID transaction2Id);

}
