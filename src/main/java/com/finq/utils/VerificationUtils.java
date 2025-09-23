package com.finq.utils;

import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Component
public class VerificationUtils {
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    public CompletableFuture<Boolean> validateAdhaar(String adhaarNumber) {
        return CompletableFuture.supplyAsync(() -> {
            simulateDelay();
            return true;
        }, executor);
    }

    public CompletableFuture<Boolean> validatePan(String panNumber) {
        return CompletableFuture.supplyAsync(() -> {
            simulateDelay();
            return true;
        }, executor);
    }

    public CompletableFuture<Boolean> validateCreditBurea(String pan) {
        return CompletableFuture.supplyAsync(() -> {
            simulateDelay();
            // assume 650+ is valid
            int creditScore = 700;
            return creditScore >= 650;
        }, executor);
    }

    private void simulateDelay() {
        try { Thread.sleep(2000); } catch (InterruptedException e) { throw new RuntimeException(e); }
    }
}
