package com.ferenc.reservation.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class DateRange {

    private LocalDate startDate;
    private LocalDate endDate;
}
