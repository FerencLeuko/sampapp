package com.ferenc.reservation.mapper;

import com.ferenc.reservation.controller.dto.BookingDto;
import com.ferenc.reservation.repository.model.Booking;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    BookingDto fromModel(Booking booking);
}
