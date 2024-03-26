package com.ferenc.reservation.businessservice;

import java.time.LocalDate;
import java.util.List;

import com.ferenc.reservation.repository.model.Booking;

public interface BookingBusinessService {

    Booking createBooking(
            String userId,
            String licencePlate,
            LocalDate startDate,
            LocalDate endDate);

    List<Booking> getAllBookings();

    List<Booking> getAllBookingsByUserId(String userId);

    Booking getBooking(Integer bookingId);

    Booking updateBooking(Integer bookingId, LocalDate startDate, LocalDate endDate);

    Booking deleteBooking(Integer bookingId);

    boolean isCarAvailable(String licencePlate, LocalDate startDate, LocalDate endDate);

}