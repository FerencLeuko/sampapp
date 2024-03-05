package com.ferenc.reservation.repository.lock;

import com.ferenc.reservation.repository.PessimisticLockRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LockServiceImpl implements LockService {

    private static final int EXPIRY_TIME_MS = 5000;

    private final Logger logger = LoggerFactory.getLogger(LockService.class);

    private final PessimisticLockRepository pessimisticLockRepository;

    @Override
    @Retryable(value = {DuplicateKeyException.class}, maxAttempts = 4, backoff = @Backoff(delay = 1000))
    public boolean acquireLock(String licencePlate, String userId) {
        PessimisticLock pessimisticLock = new PessimisticLock();
        pessimisticLock.setId(licencePlate);
        pessimisticLock.setUserId(userId);
        pessimisticLockRepository.insert(pessimisticLock);
        logger.info("Lock created: {}, at {}", licencePlate, pessimisticLock.getCreatedDate());
        return true;
    }

    @Override
    public void releaseLock(String licencePlate, String userId) {
        Optional<PessimisticLock> existingLock = pessimisticLockRepository.findById(licencePlate);
        if(existingLock.isPresent()){
            if(existingLock.get().getUserId().equals(userId)) {
                Date createdDate = existingLock.get().getCreatedDate();
                Date now = new Date();
                pessimisticLockRepository.delete(existingLock.get());
                logger.info("Lock deleted: {}, {}, at {}", licencePlate, createdDate, now);
            }
        }
    }

    @Override
    public void deleteExpiredLocks() {
        Instant now = Instant.now();
        Instant expiration = now.minusMillis(EXPIRY_TIME_MS);

        List<PessimisticLock> locks = pessimisticLockRepository.findByCreatedDateBefore(Date.from(expiration));
        pessimisticLockRepository.deleteAll(locks);
    }
}
