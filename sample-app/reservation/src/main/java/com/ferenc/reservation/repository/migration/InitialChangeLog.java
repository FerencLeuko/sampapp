package com.ferenc.reservation.repository.migration;

import java.util.ArrayList;
import java.util.List;

import com.ferenc.reservation.repository.BookingSequenceHelper;
import com.ferenc.reservation.repository.BookingSequenceRepository;
import com.ferenc.reservation.repository.CarRepository;
import com.ferenc.reservation.repository.model.BookingSequence;
import com.ferenc.reservation.repository.model.Car;
import com.ferenc.reservation.repository.model.CarTypeEnum;
import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;

@ChangeLog
public class InitialChangeLog {

    @ChangeSet(order = "001", id = "intitalBookingSequence", author = "dev")
    public void initSequence(BookingSequenceRepository bookingSequenceRepository, BookingSequenceHelper bookingSequenceHelper) {
        BookingSequence initialBookingSequence = new BookingSequence(0, bookingSequenceHelper.getBookingSequenceKey());
        bookingSequenceRepository.save(initialBookingSequence);
    }

    @ChangeSet(order = "002", id = "intitalCars", author = "dev")
    public void initCars(CarRepository carRepository) {
        List<Car> cars = new ArrayList<Car>();
        cars.add(new Car("ABC123", "Opel", "Astra", CarTypeEnum.SEDAN, 5));
        cars.add(new Car("ABC124", "Opel", "Astra", CarTypeEnum.SEDAN, 5));
        cars.add(new Car("ABC125", "Opel", "Astra", CarTypeEnum.STATION_WAGON, 5));
        carRepository.insert(cars);
    }

}
