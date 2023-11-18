package com.ferenc.reservation.businessservice;

import com.ferenc.reservation.repository.model.Car;
import com.ferenc.reservation.repository.model.CarTypeEnum;

import java.time.LocalDate;
import java.util.List;

public interface CarBusinessService {

    List<Car> getAvailableCars(LocalDate startDate,
                                      LocalDate endDate);

    List<Car> getAllCars();

    Car getCar(String licencePlate);

    Car createCar(Car car);

}
