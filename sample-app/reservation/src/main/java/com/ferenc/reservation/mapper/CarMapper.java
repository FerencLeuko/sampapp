package com.ferenc.reservation.mapper;

import com.ferenc.reservation.controller.dto.CarDto;
import com.ferenc.reservation.repository.model.Car;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CarMapper {
	
    CarDto fromModel(Car car);
    
    Car fromDto(CarDto carDto);
}
