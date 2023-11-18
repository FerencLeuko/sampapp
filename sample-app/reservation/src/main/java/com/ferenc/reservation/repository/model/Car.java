package com.ferenc.reservation.repository.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

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
