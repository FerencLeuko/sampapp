package com.ferenc.reservation.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.ferenc.reservation.AbstractTest;
import com.ferenc.reservation.controller.dto.BookingDto;
import com.ferenc.reservation.controller.dto.CarDto;
import com.ferenc.reservation.repository.model.Booking;
import com.ferenc.reservation.repository.model.Car;

class BookingMapperTest extends AbstractTest {

    private BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);

    @Test
    void fromModel() {
        Booking source = PODAM_FACTORY.manufacturePojo(Booking.class);
        BookingDto target = bookingMapper.fromModel(source);

        assertBookingDetails(target, source);
    }

    private void assertBookingDetails(BookingDto target, Booking source) {
        assertThat(target.getBookingId()).isEqualTo(source.getBookingId());
        assertThat(target.getUserId()).isEqualTo(source.getUserId());
        assertThat(target.getStartDate()).isEqualTo(source.getStartDate());
        assertThat(target.getEndDate()).isEqualTo(source.getEndDate());
        assertCarDetails(target.getCar(), source.getCar());
    }

    private void assertCarDetails(CarDto actual, Car expected) {
        assertThat(actual.getLicencePlate()).isEqualTo(expected.getLicencePlate());
        assertThat(actual.getManufacturer()).isEqualTo(expected.getManufacturer());
        assertThat(actual.getModel()).isEqualTo(expected.getModel());
        assertThat(actual.getCarType().name()).isEqualTo(expected.getCarType().name());
    }
}
