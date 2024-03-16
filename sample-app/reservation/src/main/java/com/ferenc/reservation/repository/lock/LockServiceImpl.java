package com.ferenc.reservation.repository.lock;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ferenc.reservation.repository.PessimisticLockRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LockServiceImpl implements LockService {

    private static final int EXPIRY_TIME_MS = 4000;

    private final Logger logger = LoggerFactory.getLogger(LockService.class);

    private final PessimisticLockRepository pessimisticLockRepository;

    @Override
    @Transactional()
    public boolean acquireLock(String licencePlate, String userId) {
        PessimisticLock existingLock = pessimisticLockRepository.findById(licencePlate).orElse(null);
        if (existingLock != null) {
            throw new DuplicateKeyException(String.format("Car %s currently not available.", licencePlate));
        }
        PessimisticLock pessimisticLock = new PessimisticLock();
        pessimisticLock.setId(licencePlate);
        pessimisticLock.setUserId(userId);
        pessimisticLockRepository.insert(pessimisticLock);
        logger.info("Lock created: {}, at {}", licencePlate, pessimisticLock.getCreatedDate());
        return true;
    }

    @Override
    @Transactional()
    public void releaseLock(String licencePlate, String userId) {
        Optional<PessimisticLock> existingLock = pessimisticLockRepository.findById(licencePlate);
        if (existingLock.isPresent()) {
            if (existingLock.get().getUserId().equals(userId)) {
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

        List<PessimisticLock> expiredLocks = pessimisticLockRepository.findByCreatedDateBefore(Date.from(expiration));
        if (!expiredLocks.isEmpty()) {
            pessimisticLockRepository.deleteAll(expiredLocks);
            logger.info("Locks deleted: {}, at {}.", expiredLocks.size(), now);
        }
    }
}
