package com.ferenc.reservation.controller.dto;

import com.ferenc.reservation.controller.validator.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.validation.annotation.*;

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
