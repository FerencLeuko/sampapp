package com.ferenc.reservation.repository;

import com.ferenc.reservation.repository.model.EmailEventLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailRepository extends MongoRepository<EmailEventLog,Integer> {

    Optional<EmailEventLog> findByBookingId(Integer bookingId);
}
