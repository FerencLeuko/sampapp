package com.ferenc.reservation.controller;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ferenc.reservation.controller.dto.CarDto;

@RequestMapping("/cars")
public interface CarApi {

    @GetMapping("/available")
    ResponseEntity<List<CarDto>> getAvailableCars(
            @RequestParam(name = "startDate") @JsonFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(name = "endDate") @JsonFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    );

    @GetMapping
    ResponseEntity<List<CarDto>> getAllCars();

    @GetMapping("/{licencePlate}")
    ResponseEntity<CarDto> getCar(@PathVariable("licencePlate") String licencePlate);

    @PostMapping
    ResponseEntity<CarDto> postCar(@Valid @RequestBody CarDto carDto);

}
