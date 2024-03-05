package com.ferenc.reservation.repository;

import com.ferenc.reservation.repository.lock.PessimisticLock;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface PessimisticLockRepository extends MongoRepository<PessimisticLock,String> {

    List<PessimisticLock> findByCreatedDateBefore(Date from);
}
