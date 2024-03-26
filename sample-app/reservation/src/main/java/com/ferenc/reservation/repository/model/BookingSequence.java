package com.ferenc.reservation.repository.model;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingSequence {

    private Integer sequence;

    @Id
    private String key;
}
