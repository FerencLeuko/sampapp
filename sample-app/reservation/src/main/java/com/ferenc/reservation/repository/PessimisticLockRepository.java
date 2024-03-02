package com.ferenc.reservation.repository;

import com.ferenc.reservation.repository.lock.PessimisticLock;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PessimisticLockRepository extends MongoRepository<PessimisticLock,String> {
}
