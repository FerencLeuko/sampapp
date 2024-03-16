package com.ferenc.reservation.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ferenc.reservation.repository.lock.PessimisticLock;

public interface PessimisticLockRepository extends MongoRepository<PessimisticLock, String> {

    List<PessimisticLock> findByCreatedDateBefore(Date from);
}
