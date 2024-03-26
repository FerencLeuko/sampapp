package com.ferenc.reservation.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ferenc.reservation.controller.dto.BookingDto;
import com.ferenc.reservation.controller.dto.BookingRequest;
import com.ferenc.reservation.controller.dto.UpdateRequest;

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
