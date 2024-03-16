package com.ferenc.reservation.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class CarDto {

    @NotBlank
    private String licencePlate;

    @NotBlank
    private String manufacturer;

    @NotBlank
    private String model;

    @NotNull
    private CarTypeEnum carType;

    @Min(1)
    private int numberOfPerson;
}
