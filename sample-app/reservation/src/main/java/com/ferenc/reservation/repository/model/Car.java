package com.ferenc.reservation.repository.model;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Car {

    @Id
    private String licencePlate;

    private String manufacturer;

    private String model;

    private CarTypeEnum carType;

    private int numberOfPerson;
}
