package com.ferenc.reservation.mapper;

import com.ferenc.reservation.controller.dto.BookingDto;
import com.ferenc.reservation.controller.dto.CarDto;
import com.ferenc.reservation.controller.dto.CarTypeEnum;
import com.ferenc.reservation.repository.model.Booking;
import com.ferenc.reservation.repository.model.Car;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.*;

import java.time.LocalDate;

public class BookingMapperTest {

    private BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);

    @Test
    void testMapping(){
        CarDto carDto = new CarDto();
        carDto.setLicencePlate("ABC-123");
        carDto.setManufacturer("Opel");
        carDto.setModel("Astra");
        carDto.setCarType(CarTypeEnum.SEDAN);

        BookingDto expected = new BookingDto();
        expected.setBookingId(1);
        expected.setUserId("abc@google.com");
        expected.setStartDate(LocalDate.now().plusDays(1));
        expected.setEndDate(LocalDate.now().plusDays(2));
        expected.setCar(carDto);

        Car car = new Car();
        car.setLicencePlate("ABC-123");
        car.setManufacturer("Opel");
        car.setModel("Astra");
        car.setCarType(com.ferenc.reservation.repository.model.CarTypeEnum.SEDAN);

        Booking model = new Booking();
        model.setBookingId(1);
        model.setUserId("abc@google.com");
        model.setStartDate(LocalDate.now().plusDays(1));
        model.setEndDate(LocalDate.now().plusDays(2));
        model.setCar(car);

        BookingDto actual = bookingMapper.fromModel(model);

        Assertions.assertEquals(expected.getBookingId(),actual.getBookingId());
        Assertions.assertEquals(expected.getUserId(), actual.getUserId());
        Assertions.assertEquals(expected.getStartDate(),actual.getStartDate());
        Assertions.assertEquals(expected.getEndDate(),actual.getEndDate());
        Assertions.assertEquals(expected.getCar(),actual.getCar());
    }
}
