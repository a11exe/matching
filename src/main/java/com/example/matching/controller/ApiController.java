package com.example.matching.controller;

import com.example.matching.model.Transaction1;
import com.example.matching.model.Transaction2;
import com.example.matching.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RestController
public class ApiController {

    private static final Logger log = LoggerFactory.getLogger(InitService.class);

    private final Transaction1Receiver transaction1Receiver;
    private final Transaction2Receiver transaction2Receiver;
    private final MatchTransaction1Service matchTransaction1Service;
    private final MatchTransaction2Service matchTransaction2Service;
    private final MatchTransaction1ServiceConcurrent matchTransaction1ServiceConcurrent;
    private final MatchTransaction2ServiceConcurrent matchTransaction2ServiceConcurrent;
    private final InitService initService;
    private final CheckMatchingService checkMatchingService;
    @Value( "${transactions.count}" )
    private Integer transactionsCount;

    public ApiController(Transaction1Receiver transaction1Receiver,
                         Transaction2Receiver transaction2Receiver,
                         MatchTransaction1Service matchTransaction1Service,
                         MatchTransaction2Service matchTransaction2Service,
                         MatchTransaction1ServiceConcurrent matchTransaction1ServiceConcurrent,
                         MatchTransaction2ServiceConcurrent matchTransaction2ServiceConcurrent,
                         InitService initService,
                         CheckMatchingService checkMatchingService) {
        this.transaction1Receiver = transaction1Receiver;
        this.transaction2Receiver = transaction2Receiver;
        this.matchTransaction1Service = matchTransaction1Service;
        this.matchTransaction2Service = matchTransaction2Service;
        this.matchTransaction1ServiceConcurrent = matchTransaction1ServiceConcurrent;
        this.matchTransaction2ServiceConcurrent = matchTransaction2ServiceConcurrent;
        this.initService = initService;
        this.checkMatchingService = checkMatchingService;
    }

    @GetMapping("/api/match/1")
    public void startMatchingSide1() {
        log.info("Start matching 1, 2");
        initService.clearData();
        int transaction1Count = transaction1Count();
        for (int i = 0; i < transaction1Count; i++) {
            Transaction1 transaction1 = transaction1Receiver.receive();
            matchTransaction1Service.startMatching(transaction1);
        }
        int transaction2Count = transaction2Count();
        for (int i = 0; i < transaction2Count; i++) {
            Transaction2 transaction2 = transaction2Receiver.receive();
            matchTransaction2Service.startMatching(transaction2);
        }
        checkMatchingService.checkMatchingResult();
    }

    @GetMapping("/api/match/2")
    public void startMatchingSide2() {
        log.info("Start matching 2, 1");
        initService.clearData();
        int transaction1Count = transaction1Count();
        for (int i = 0; i < transaction1Count; i++) {
            Transaction2 transaction2 = transaction2Receiver.receive();
            matchTransaction2Service.startMatching(transaction2);
        }
        int transaction2Count = transaction2Count();
        for (int i = 0; i < transaction2Count; i++) {
            Transaction1 transaction1 = transaction1Receiver.receive();
            matchTransaction1Service.startMatching(transaction1);
        }
        checkMatchingService.checkMatchingResult();
    }

    @GetMapping("/api/match/parallel")
    public void startMatchingParallel() throws ExecutionException, InterruptedException {
        log.info("Start matching parallel");
        long start = System.currentTimeMillis();
        initService.clearData();
        int transaction1Count = transaction1Count();
        int transaction2Count = transaction2Count();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Future<String> future1 = executorService.submit(() -> {
            for (int i = 0; i < transaction1Count; i++) {
                Transaction1 transaction1 = transaction1Receiver.receive();
                matchTransaction1Service.startMatching(transaction1);
            }
            return "Finish 1";
        });
        Future<String> future2 = executorService.submit(() -> {
            for (int i = 0; i < transaction2Count; i++) {
                Transaction2 transaction2 = transaction2Receiver.receive();
                matchTransaction2Service.startMatching(transaction2);
            }
            return "Finish 2";
        });
        String result1 = future1.get();
        log.info("{}", result1);
        String resul2 = future2.get();
        log.info("{}", resul2);

        long totalTime = (System.currentTimeMillis() - start) / 1000;
        log.info("Matching time {} seconds", totalTime);

        checkMatchingService.checkMatchingResult();
    }

    @GetMapping("/api/match/concurrent/parallel")
    public void startMatchingConcurrentParallel() throws ExecutionException, InterruptedException {
        log.info("Start matching concurrent parallel");
        int transaction1Count = transaction1Count();
        int transaction2Count = transaction2Count();
        long start = System.currentTimeMillis();
        initService.clearData();
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        Future<String> future1 = executorService.submit(() -> {
            for (int i = 0; i < transaction1Count; i++) {
                Transaction1 transaction1 = transaction1Receiver.receive();
                matchTransaction1ServiceConcurrent.startMatching(transaction1);
            }
            return "Finish 1";
        });
        Future<String> future2 = executorService.submit(() -> {
            for (int i = 0; i < transaction2Count; i++) {
                Transaction2 transaction2 = transaction2Receiver.receive();
                matchTransaction2ServiceConcurrent.startMatching(transaction2);
            }
            return "Finish 2";
        });
        String result1 = future1.get();
        log.info("{}", result1);
        String result2 = future2.get();
        log.info("{}", result2);

        long totalTime = (System.currentTimeMillis() - start) / 1000;
        log.info("Matching time {} seconds", totalTime);

        checkMatchingService.checkMatchingResult();
    }

    private int transaction1Count() {
        return (transactionsCount / 3) * 2;
    }

    private int transaction2Count() {
        return transactionsCount / 3;
    }
}
