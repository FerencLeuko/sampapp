package com.ferenc.reservation.repository.lock;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LockManager {

    private final LockService lockService;

    @Scheduled(fixedRate = 60000)
    public void releaseExpiredLocks() {
        lockService.deleteExpiredLocks();
    }
}
