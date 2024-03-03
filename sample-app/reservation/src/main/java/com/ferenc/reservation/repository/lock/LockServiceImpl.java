package com.ferenc.reservation.repository.lock;

import com.ferenc.reservation.repository.PessimisticLockRepository;
import com.ferenc.reservation.repository.lock.LockService;
import com.ferenc.reservation.repository.lock.PessimisticLock;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LockServiceImpl implements LockService {

    private final Logger logger = LoggerFactory.getLogger(LockService.class);

    private final PessimisticLockRepository pessimisticLockRepository;

    @Override
    @Retryable(value = {DuplicateKeyException.class}, maxAttempts = 4, backoff = @Backoff(delay = 1000))
    public void acquireLock(String licencePlate) {
        Optional<PessimisticLock> existingLock = pessimisticLockRepository.findById(licencePlate);
        if(existingLock.isPresent()){
            Date createdDate = existingLock.get().getCreatedDate();
            Date now = new Date();
            if(now.getTime() - createdDate.getTime() > 3000 ){
                pessimisticLockRepository.delete(existingLock.get());
                logger.info("Lock deleted: {}, {}, at {}", licencePlate, createdDate, now);
            }
        }
        PessimisticLock pessimisticLock = new PessimisticLock();
        pessimisticLock.setId(licencePlate);
        pessimisticLockRepository.insert(pessimisticLock);
        logger.info("Lock created: {}, at {}", licencePlate, pessimisticLock.getCreatedDate());
    }

    @Override
    public void releaseLock(String licencePlate) {
        Optional<PessimisticLock> existingLock = pessimisticLockRepository.findById(licencePlate);
        if(existingLock.isPresent()){
            Date createdDate = existingLock.get().getCreatedDate();
            Date now = new Date();
            pessimisticLockRepository.delete(existingLock.get());
            logger.info("Lock deleted: {}, {}, at {}", licencePlate, createdDate, now);
        }
    }
}
