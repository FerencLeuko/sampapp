package com.ferenc.reservation.mapper;

import org.mapstruct.Mapper;

import com.ferenc.reservation.controller.dto.CarDto;
import com.ferenc.reservation.repository.model.Car;

@Mapper(componentModel = "spring")
public interface CarMapper {

    CarDto fromModel(Car car);

    Car fromDto(CarDto carDto);
}
