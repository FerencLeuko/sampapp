package com.ferenc.reservation.controller.dto;

import java.time.LocalDate;

import org.springframework.validation.annotation.Validated;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class DateRange {

    private LocalDate startDate;
    private LocalDate endDate;
}
