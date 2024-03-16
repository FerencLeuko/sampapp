package com.ferenc.reservation.mapper;

import org.mapstruct.Mapper;

import com.ferenc.reservation.controller.dto.CarTypeEnum;

@Mapper(componentModel = "spring")
public interface CarTypeMapper {

    CarTypeEnum fromModel(com.ferenc.reservation.repository.model.CarTypeEnum carTypeEnum);

    com.ferenc.reservation.repository.model.CarTypeEnum fromDto(CarTypeEnum carTypeEnum);
}
