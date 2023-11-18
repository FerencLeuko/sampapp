package com.ferenc.reservation.repository;

import com.ferenc.reservation.repository.model.BookingSequence;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingSequenceRepository extends MongoRepository<BookingSequence,String> {
    Optional<BookingSequence> findByKey(String key);
}
