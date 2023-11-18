package com.ferenc.commons.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingEvent implements Serializable {

    private int bookingId;

    private LocalDateTime timeCreated;

    private String userId;

    private String lisencePlate;

    private LocalDate startDate;

    private LocalDate endDate;

}
