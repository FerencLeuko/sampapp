package com.ferenc.reservation.repository.lock;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class LockServiceImpl implements LockService {

    private static final String LOCK_PREFIX = "carLock:";
    private static final long LOCK_EXPIRY_TIME_SECONDS = 5;

    private final Logger logger = LoggerFactory.getLogger(LockService.class);
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    @Transactional
    @Retryable(value = { DuplicateKeyException.class }, maxAttempts = 3, backoff = @Backoff(delay = 500))
    public boolean acquireLock(String licencePlate, String userId) {
        String lockKey = LOCK_PREFIX + licencePlate;

        boolean lockAcquired = redisTemplate.opsForValue().setIfAbsent(lockKey, userId, LOCK_EXPIRY_TIME_SECONDS, TimeUnit.SECONDS);

        if (lockAcquired) {
            logger.info("Lock acquired: {}, by user {}, at {}", licencePlate, userId, Instant.now());
            return true;
        } else {
            throw new DuplicateKeyException(String.format("Car %s currently not available.", licencePlate));
        }
    }

    @Override
    public void releaseLock(String licencePlate, String userId) {
        String lockKey = LOCK_PREFIX + licencePlate;

        String storedUserId = redisTemplate.opsForValue().get(lockKey);
        if (userId.equals(storedUserId)) {
            redisTemplate.delete(lockKey);
            logger.info("Lock released: {}, by user {}, at {}", licencePlate, userId, Instant.now());
        }
    }
}
