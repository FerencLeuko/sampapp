package com.ferenc.reservation.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ferenc.reservation.controller.dto.CarDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequestMapping("/cars")
public interface CarApi {
	
    @GetMapping("/available")
    ResponseEntity<List<CarDto>> getAvailableCars(
            @RequestParam(name="startDate") @JsonFormat(pattern="yyyy-MM-dd") LocalDate startDate,
            @RequestParam(name="endDate") @JsonFormat(pattern="yyyy-MM-dd") LocalDate endDate
    );

    @GetMapping
    ResponseEntity<List<CarDto>> getAllCars();

    @GetMapping("/{licencePlate}")
    ResponseEntity<CarDto> getCar(@PathVariable("licencePlate")String licencePlate);

    @PostMapping
    ResponseEntity<CarDto> postCar(@Valid @RequestBody CarDto carDto);

}
