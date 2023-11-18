package com.ferenc.reservation.repository;

import com.ferenc.reservation.repository.model.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends MongoRepository<Booking,Integer> {
    
    Optional<Booking> findByBookingId(Integer bookingId);

    List<Booking> findByCarLicencePlate(String licencePlate);

    List<Booking> findByUserId(String userId);

}
