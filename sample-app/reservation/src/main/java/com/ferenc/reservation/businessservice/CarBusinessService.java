package com.ferenc.reservation.businessservice;

import java.time.LocalDate;
import java.util.List;

import com.ferenc.reservation.repository.model.Car;

public interface CarBusinessService {

    List<Car> getAvailableCars(LocalDate startDate,
            LocalDate endDate);

    List<Car> getAllCars();

    Car getCar(String licencePlate);

    Car createCar(Car car);

}
