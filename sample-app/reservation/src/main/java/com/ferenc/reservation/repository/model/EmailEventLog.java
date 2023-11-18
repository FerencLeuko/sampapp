package com.ferenc.reservation.repository.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailEventLog {

    private int bookingId;

    private LocalDateTime emailSent;
}
