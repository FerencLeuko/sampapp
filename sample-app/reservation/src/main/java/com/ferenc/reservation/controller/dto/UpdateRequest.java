package com.ferenc.reservation.controller.dto;

import org.springframework.validation.annotation.Validated;

import com.ferenc.reservation.controller.validator.ValidRange;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class UpdateRequest {

    @ValidRange
    private DateRange dateRange;
}
