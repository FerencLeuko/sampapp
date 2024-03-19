package com.ferenc.reservation.businessservice;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ferenc.reservation.exception.NoSuchCarException;
import com.ferenc.reservation.repository.CarRepository;
import com.ferenc.reservation.repository.model.Car;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CarBusinessServiceImpl implements CarBusinessService {

    private final Logger logger = LoggerFactory.getLogger(CarBusinessService.class);

    private final CarRepository carRepository;

    private final BookingBusinessService bookingBusinessService;

    @Override
    public List<Car> getAvailableCars(
            LocalDate startDate,
            LocalDate endDate
    ) {
        List<Car> cars = carRepository.findAll();
        cars = cars
                .stream()
                .filter(car -> bookingBusinessService.isCarAvailable(car.getLicencePlate(), startDate, endDate))
                .collect(Collectors.toList());
        return cars;
    }

    @Override
    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    @Override
    public Car getCar(String licencePlate) {
        return carRepository.findByLicencePlate(licencePlate)
                .orElseThrow(() -> new NoSuchCarException("This car does not exist in our system: " + licencePlate + "."));
    }

    @Override
    public Car createCar(Car car) {
        carRepository.save(car);
        logger.info("Car has been created, licencePlate: {}", car.getLicencePlate());
        return car;
    }

}
