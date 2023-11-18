package com.ferenc.reservation.repository;

import com.ferenc.reservation.repository.model.Car;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarRepository extends MongoRepository<Car,String> {
    Optional<Car> findByLicencePlate(String licencePlate);
}
