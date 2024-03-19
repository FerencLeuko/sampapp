package com.ferenc.reservation.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.ferenc.reservation.AbstractTest;
import com.ferenc.reservation.controller.dto.BookingDto;
import com.ferenc.reservation.controller.dto.CarDto;
import com.ferenc.reservation.controller.dto.CarTypeEnum;
import com.ferenc.reservation.repository.model.Booking;
import com.ferenc.reservation.repository.model.Car;

class BookingMapperTest extends AbstractTest {

    private BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);

    @Test
    void fromModel(){
        Booking source = PODAM_FACTORY.manufacturePojo(Booking.class);
        BookingDto target = bookingMapper.fromModel(source);

        assertBookingDetails(source,target);
    }

    private void assertBookingDetails(Booking source, BookingDto target) {
        assertThat(source.getBookingId()).isEqualTo(target.getBookingId());
        assertThat(source.getUserId()).isEqualTo(target.getUserId());
        assertThat(source.getStartDate()).isEqualTo(target.getStartDate());
        assertThat(source.getEndDate()).isEqualTo(target.getEndDate());
        assertCarDetails(source.getCar(), target.getCar());
    }

    private void assertCarDetails(Car expected, CarDto actual) {
        assertThat(expected.getLicencePlate()).isEqualTo(actual.getLicencePlate());
        assertThat(expected.getManufacturer()).isEqualTo(actual.getManufacturer());
        assertThat(expected.getModel()).isEqualTo(actual.getModel());
        assertThat(expected.getCarType().name()).isEqualTo(actual.getCarType().name());
    }
}
