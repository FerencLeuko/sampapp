package com.ferenc.reservation.mapper;

import org.mapstruct.Mapper;

import com.ferenc.reservation.controller.dto.BookingDto;
import com.ferenc.reservation.repository.model.Booking;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    BookingDto fromModel(Booking booking);
}
