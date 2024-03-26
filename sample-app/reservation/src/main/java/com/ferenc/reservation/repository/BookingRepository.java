package com.ferenc.reservation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ferenc.reservation.repository.model.Booking;

@Repository
public interface BookingRepository extends MongoRepository<Booking, Integer> {

    Optional<Booking> findByBookingId(Integer bookingId);

    List<Booking> findByCarLicencePlate(String licencePlate);

    List<Booking> findByUserId(String userId);

}
