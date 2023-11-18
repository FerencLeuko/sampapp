package com.ferenc.reservation.controller.dto;

import com.ferenc.reservation.controller.validator.ValidRange;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class UpdateRequest {

    @ValidRange
    private DateRange dateRange;
}
