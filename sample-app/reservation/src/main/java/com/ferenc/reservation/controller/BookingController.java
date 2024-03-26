package com.ferenc.reservation.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import com.ferenc.reservation.auth.SecurityUtils;
import com.ferenc.reservation.businessservice.BookingBusinessService;
import com.ferenc.reservation.controller.dto.BookingDto;
import com.ferenc.reservation.controller.dto.BookingRequest;
import com.ferenc.reservation.controller.dto.UpdateRequest;
import com.ferenc.reservation.mapper.BookingMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BookingController implements BookingApi {

    private final BookingBusinessService bookingBusinessService;
    private final BookingMapper bookingMapper = getBookingMapper();
    @Value("${server.bookings.uri}")
    private String bookingsURI;

    private static String getUserId() {
        return SecurityUtils.getUserEmailFromToken();
    }

    protected static BookingMapper getBookingMapper() {
        return Mappers.getMapper(BookingMapper.class);
    }

    @Override
    public ResponseEntity<BookingDto> postBooking(BookingRequest bookingRequest) {
        BookingDto response = bookingMapper.fromModel(
                bookingBusinessService
                        .createBooking(
                                getUserId(),
                                bookingRequest.getLicencePlate(),
                                bookingRequest.getDateRange().getStartDate(),
                                bookingRequest.getDateRange().getEndDate()));
        URI uri = URI.create(bookingsURI.concat(String.valueOf(response.getBookingId())));
        return ResponseEntity.created(uri).build();
    }

    @Override
    public ResponseEntity<List<BookingDto>> getAllBookings() {
        List<BookingDto> response =
                bookingBusinessService.getAllBookingsByUserId(getUserId())
                        .stream()
                        .map(bookingMapper::fromModel)
                        .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @Override
    @PostAuthorize("returnObject.body.userId==authentication.principal.claims['email']")
    public ResponseEntity<BookingDto> getBooking(Integer bookingId) {
        BookingDto response = bookingMapper.fromModel(bookingBusinessService.getBooking(bookingId));
        return ResponseEntity.ok(response);
    }

    @Override
    @PreAuthorize("@bookingBusinessServiceImpl.getBooking(#bookingId).userId==authentication.principal.claims['email']")
    public ResponseEntity<BookingDto> updateBooking(Integer bookingId, UpdateRequest updateRequest) {
        BookingDto response =
                bookingMapper.fromModel(
                        bookingBusinessService.updateBooking(
                                bookingId,
                                updateRequest.getDateRange().getStartDate(),
                                updateRequest.getDateRange().getEndDate()));
        return ResponseEntity.ok(response);
    }

    @Override
    @PreAuthorize("@bookingBusinessServiceImpl.getBooking(#bookingId).userId==authentication.principal.claims['email']")
    public ResponseEntity<BookingDto> deleteBooking(Integer bookingId) {
        BookingDto response =
                bookingMapper.fromModel(
                        bookingBusinessService
                                .deleteBooking(bookingId));
        return ResponseEntity.ok(response);
    }

}
