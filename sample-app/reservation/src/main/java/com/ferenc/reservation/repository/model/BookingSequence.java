package com.ferenc.reservation.repository.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingSequence {

    private Integer sequence;

    @Id
    private String key;
}
