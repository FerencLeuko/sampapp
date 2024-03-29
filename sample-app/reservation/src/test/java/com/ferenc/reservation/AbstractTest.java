package com.ferenc.reservation;

import static com.ferenc.reservation.TestConstants.END_DATE;
import static com.ferenc.reservation.TestConstants.LICENCE_PLATE;
import static com.ferenc.reservation.TestConstants.LICENCE_PLATE_OTHER;
import static com.ferenc.reservation.TestConstants.START_DATE;

import java.util.Set;

import com.ferenc.reservation.controller.dto.BookingRequest;
import com.ferenc.reservation.controller.dto.DateRange;
import com.ferenc.reservation.controller.dto.UpdateRequest;
import com.ferenc.reservation.repository.model.Car;
import com.ferenc.reservation.repository.model.CarTypeEnum;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

public abstract class AbstractTest {

    protected static final PodamFactory PODAM_FACTORY = new PodamFactoryImpl();

    protected static BookingRequest getValidBookingRequest() {
        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setLicencePlate(LICENCE_PLATE);
        bookingRequest.setDateRange(new DateRange(START_DATE, END_DATE));
        return bookingRequest;
    }

    protected static UpdateRequest getValidUpdateRequest() {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.setDateRange(new DateRange(START_DATE, END_DATE));
        return updateRequest;
    }

    protected static Set<Car> getCars() {
        return Set.of(
                new Car(LICENCE_PLATE, "Opel", "Astra", CarTypeEnum.SEDAN, 5),
                new Car(LICENCE_PLATE_OTHER, "Opel", "Astra", CarTypeEnum.SEDAN, 5));

    }
}
