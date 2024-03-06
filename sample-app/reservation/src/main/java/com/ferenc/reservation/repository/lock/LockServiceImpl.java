package com.ferenc.reservation.repository.lock;

import com.ferenc.reservation.repository.PessimisticLockRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

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

    private final MongoTransactionManager transactionManager;

    @Override
    @Transactional
    @Retryable(value = {DataAccessException.class}, maxAttempts = 4, backoff = @Backoff(delay = 300))
    public boolean acquireLock(String licencePlate, String userId) {
        TransactionStatus transactionStatus = transactionManager.getTransaction(null);
        PessimisticLock existingLock = pessimisticLockRepository.findById(licencePlate).orElse(null);
        try{
            if (existingLock != null) {
                throw new DuplicateKeyException(String.format("Car %s currently not available.", licencePlate ));
            }
            PessimisticLock pessimisticLock = new PessimisticLock();
            pessimisticLock.setId(licencePlate);
            pessimisticLock.setUserId(userId);
            pessimisticLockRepository.insert(pessimisticLock);
            logger.info("Lock created: {}, at {}", licencePlate, pessimisticLock.getCreatedDate());
            transactionManager.commit(transactionStatus);
            return true;
        } catch (Throwable e) {
            transactionManager.rollback(transactionStatus);
            throw e;
        }
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
        if(locks.size()>0) {
            pessimisticLockRepository.deleteAll(locks);
            logger.info("Locks deleted: {}, at {}.", locks.size(), now);
        }
    }
}
