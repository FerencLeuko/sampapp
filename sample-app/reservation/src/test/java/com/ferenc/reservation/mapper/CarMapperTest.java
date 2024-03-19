package com.ferenc.reservation.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.ferenc.reservation.AbstractTest;
import com.ferenc.reservation.controller.dto.CarDto;
import com.ferenc.reservation.controller.dto.CarTypeEnum;
import com.ferenc.reservation.repository.model.Car;

class CarMapperTest extends AbstractTest {

    private final CarMapper carMapper = Mappers.getMapper(CarMapper.class);

    @Test
    void fromModel(){
        Car source = PODAM_FACTORY.manufacturePojo(Car.class);
        CarDto target = carMapper.fromModel(source);

        assertCarDetails(target, source);
    }

    @Test
    void fromMDTO(){
        CarDto source = PODAM_FACTORY.manufacturePojo(CarDto.class);
        Car target = carMapper.fromDto(source);

        assertCarDetails(source, target);
    }

    private void assertCarDetails(CarDto carDto, Car car) {
        assertThat(carDto.getLicencePlate()).isEqualTo(car.getLicencePlate());
        assertThat(carDto.getManufacturer()).isEqualTo(car.getManufacturer());
        assertThat(carDto.getModel()).isEqualTo(car.getModel());
        assertThat(carDto.getNumberOfPerson()).isEqualTo(car.getNumberOfPerson());
        assertThat(carDto.getCarType().name()).isEqualTo(car.getCarType().name());
    }
}
