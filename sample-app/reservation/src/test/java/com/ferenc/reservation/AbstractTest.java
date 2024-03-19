package com.ferenc.reservation;

import java.time.LocalDate;

import com.ferenc.reservation.controller.dto.BookingRequest;
import com.ferenc.reservation.controller.dto.DateRange;
import com.ferenc.reservation.controller.dto.UpdateRequest;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

public class AbstractTest {

    protected static final PodamFactory PODAM_FACTORY = new PodamFactoryImpl();
    protected static final int TEST_BOOKING_ID = 1;
    protected static final String TEST_USER_ID = "abc@google.com";
    protected static final String OTHER_USER_ID = "foo" + TEST_USER_ID;
    protected static final String TEST_LICENCE_PLATE = "ABC123";
    protected static final LocalDate START_DATE = LocalDate.now();
    protected static final LocalDate END_DATE = LocalDate.now().plusDays(1);

    protected static BookingRequest getValidBookingRequest() {
        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setLicencePlate(TEST_LICENCE_PLATE);
        LocalDate startDate = START_DATE;
        LocalDate endDate = END_DATE;
        DateRange dateRange = new DateRange(startDate, endDate);
        bookingRequest.setDateRange(dateRange);
        return bookingRequest;
    }

    protected static UpdateRequest getValidUpdateRequest() {
        UpdateRequest updateRequest = new UpdateRequest();
        DateRange dateRange = new DateRange(START_DATE, END_DATE);
        updateRequest.setDateRange(dateRange);
        return updateRequest;
    }

}
