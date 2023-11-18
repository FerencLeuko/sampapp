package com.ferenc.reservation.mapper;

import com.ferenc.reservation.controller.dto.CarDto;
import com.ferenc.reservation.controller.dto.CarTypeEnum;
import com.ferenc.reservation.repository.model.Car;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.*;

public class CarMapperTest {
	
    private final CarMapper carMapper = Mappers.getMapper(CarMapper.class);

    @Test
    void testMapping(){
        CarDto expected = new CarDto();
        expected.setLicencePlate("ABC-123");
        expected.setManufacturer("Opel");
        expected.setModel("Astra");
        expected.setNumberOfPerson(5);
        expected.setCarType(CarTypeEnum.SEDAN);

        Car model = new Car();
        model.setLicencePlate("ABC-123");
        model.setManufacturer("Opel");
        model.setModel("Astra");
        model.setNumberOfPerson(5);
        model.setCarType(com.ferenc.reservation.repository.model.CarTypeEnum.SEDAN);

        CarDto actual = carMapper.fromModel(model);

        Assertions.assertEquals(expected.getLicencePlate(), actual.getLicencePlate());
        Assertions.assertEquals(expected.getManufacturer(), actual.getManufacturer());
        Assertions.assertEquals(expected.getModel(),actual.getModel());
        Assertions.assertEquals(expected.getNumberOfPerson(), actual.getNumberOfPerson());
        Assertions.assertEquals(expected.getCarType(),actual.getCarType());
        
        expected = carMapper.fromModel(carMapper.fromDto(actual));
        
        Assertions.assertEquals(expected,actual);
    }
}
