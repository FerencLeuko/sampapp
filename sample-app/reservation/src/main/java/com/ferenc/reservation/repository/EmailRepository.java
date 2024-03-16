package com.ferenc.reservation.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ferenc.reservation.repository.model.EmailEventLog;

@Repository
public interface EmailRepository extends MongoRepository<EmailEventLog, Integer> {

    Optional<EmailEventLog> findByBookingId(Integer bookingId);
}
