package com.ferenc.reservation.repository.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.sql.Ref;
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

    @DBRef
    private Car car;
}
