package com.ferenc.reservation.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ferenc.reservation.repository.model.BookingSequence;

@Repository
public interface BookingSequenceRepository extends MongoRepository<BookingSequence, String> {

    Optional<BookingSequence> findByKey(String key);
}
