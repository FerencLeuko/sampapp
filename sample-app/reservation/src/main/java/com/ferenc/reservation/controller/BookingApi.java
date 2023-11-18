package com.ferenc.reservation.controller;

import com.ferenc.reservation.controller.dto.BookingDto;
import com.ferenc.reservation.controller.dto.BookingRequest;
import com.ferenc.reservation.controller.dto.UpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/bookings")
public interface BookingApi {

    @PostMapping()
    ResponseEntity<BookingDto> postBooking(
            @Valid @RequestBody BookingRequest bookingRequest);

    @GetMapping()
    ResponseEntity<List<BookingDto>> getAllBookings();

    @GetMapping("/{bookingId}")
    ResponseEntity<BookingDto> getBooking(
            @PathVariable("bookingId") Integer bookingId);

    @PutMapping("/{bookingId}")
    ResponseEntity<BookingDto> updateBooking(
            @PathVariable("bookingId") Integer bookingId,
            @Valid @RequestBody UpdateRequest updateRequest
            );

    @DeleteMapping("/{bookingId}")
    ResponseEntity<BookingDto> deleteBooking(
            @PathVariable("bookingId") Integer bookingId);
}
