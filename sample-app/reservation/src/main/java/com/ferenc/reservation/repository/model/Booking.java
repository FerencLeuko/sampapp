package com.ferenc.reservation.repository.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking {

    @Id
    private Integer bookingId;

    private String userId;

    private LocalDate startDate;

    private LocalDate endDate;

    private Car car;
}
