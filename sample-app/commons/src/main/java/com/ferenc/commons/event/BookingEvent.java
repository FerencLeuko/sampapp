package com.ferenc.commons.event;

import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Value
@Builder
public class BookingEvent implements Serializable {

    private int bookingId;

    private LocalDateTime timeCreated;

    private String userId;

    private String lisencePlate;

    private LocalDate startDate;

    private LocalDate endDate;
}
