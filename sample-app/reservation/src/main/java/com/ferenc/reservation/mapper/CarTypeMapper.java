package com.ferenc.reservation.mapper;

import com.ferenc.reservation.controller.dto.CarTypeEnum;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CarTypeMapper {

    CarTypeEnum fromModel(com.ferenc.reservation.repository.model.CarTypeEnum carTypeEnum);

    com.ferenc.reservation.repository.model.CarTypeEnum fromDto(CarTypeEnum carTypeEnum);
}
