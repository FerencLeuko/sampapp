package com.ferenc.reservation.repository.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailEventLog {

    private int bookingId;

    private LocalDateTime emailSent;
}
