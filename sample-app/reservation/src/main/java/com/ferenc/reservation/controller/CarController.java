package com.ferenc.reservation.controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import com.ferenc.reservation.businessservice.CarBusinessService;
import com.ferenc.reservation.controller.dto.CarDto;
import com.ferenc.reservation.mapper.CarMapper;
import com.ferenc.reservation.mapper.CarTypeMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CarController implements CarApi {

    private final CarBusinessService carBusinessService;

    private final CarTypeMapper carTypeMapper = getCarTypeMapper();

    private final CarMapper carMapper = getCarMapper();

    @Value("${server.cars.uri}")
    private String carsURI;

    protected static CarMapper getCarMapper() {
        return Mappers.getMapper(CarMapper.class);
    }

    protected static CarTypeMapper getCarTypeMapper() {
        return Mappers.getMapper(CarTypeMapper.class);
    }

    @Override
    public ResponseEntity<List<CarDto>> getAvailableCars(LocalDate startDate, LocalDate endDate) {
        List<CarDto> responseList =
                carBusinessService.getAvailableCars(startDate, endDate)
                        .stream()
                        .map(carMapper::fromModel)
                        .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('admin')")
    public ResponseEntity<List<CarDto>> getAllCars() {
        List<CarDto> response = carBusinessService.getAllCars()
                .stream()
                .map(carMapper::fromModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('admin')")
    public ResponseEntity<CarDto> getCar(String licencePlate) {
        CarDto response = carMapper.fromModel(carBusinessService.getCar(licencePlate));
        return ResponseEntity.ok(response);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('admin')")
    public ResponseEntity<CarDto> postCar(CarDto carDto) {
        CarDto response = carMapper.fromModel(
                carBusinessService.createCar(carMapper.fromDto(carDto)));
        URI uri = URI.create(carsURI.concat(String.valueOf(response.getLicencePlate())));
        return ResponseEntity.created(uri).build();
    }

}
