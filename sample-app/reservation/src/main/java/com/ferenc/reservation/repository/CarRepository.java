package com.ferenc.reservation.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ferenc.reservation.repository.model.Car;

@Repository
public interface CarRepository extends MongoRepository<Car, String> {

    Optional<Car> findByLicencePlate(String licencePlate);
}
