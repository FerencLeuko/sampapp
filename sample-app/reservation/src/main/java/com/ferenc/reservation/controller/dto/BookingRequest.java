package com.ferenc.reservation.controller.dto;

import jakarta.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;

import com.ferenc.reservation.controller.validator.ValidRange;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class BookingRequest {

    @NotBlank
    private String licencePlate;

    @ValidRange
    private DateRange dateRange;
}
