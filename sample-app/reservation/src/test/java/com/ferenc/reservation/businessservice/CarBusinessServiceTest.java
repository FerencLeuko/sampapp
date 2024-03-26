package com.ferenc.reservation.businessservice;

import static com.ferenc.reservation.TestConstants.END_DATE;
import static com.ferenc.reservation.TestConstants.START_DATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ferenc.reservation.AbstractTest;
import com.ferenc.reservation.exception.NoSuchCarException;
import com.ferenc.reservation.repository.CarRepository;
import com.ferenc.reservation.repository.model.Car;

@ExtendWith(MockitoExtension.class)
class CarBusinessServiceTest extends AbstractTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private BookingBusinessService bookingBusinessService;

    @InjectMocks
    private CarBusinessServiceImpl carBusinessService;

    @AfterEach
    void verifyMocks() {
        verifyNoMoreInteractions(carRepository, bookingBusinessService);
    }

    @Test
    void testGetAvailableCars() {
        List<Car> expected = PODAM_FACTORY.manufacturePojo(ArrayList.class, Car.class);
        when(carRepository.findAll()).thenReturn(expected);
        when(bookingBusinessService.isCarAvailable(any(), any(), any())).thenReturn(true);
        List<Car> actual = carBusinessService.getAvailableCars(START_DATE, END_DATE);

        assertThat(expected).isEqualTo(actual);

        verify(carRepository).findAll();
        verify(bookingBusinessService, times(expected.size())).isCarAvailable(any(), any(), any());
    }

    @Test
    void testGetAllCars() {
        List<Car> expected = PODAM_FACTORY.manufacturePojo(ArrayList.class, Car.class);
        when(carRepository.findAll()).thenReturn(expected);
        List<Car> actual = carBusinessService.getAllCars();

        assertThat(expected).isEqualTo(actual);

        verify(carRepository).findAll();
    }

    @Test
    void testGetCar() {
        Car car = PODAM_FACTORY.manufacturePojo(Car.class);
        String licencePlate = car.getLicencePlate();
        when(carRepository.findByLicencePlate(any())).thenReturn(Optional.of(car));

        Car retrievedCar = carBusinessService.getCar(licencePlate);

        assertThat(car).isEqualTo(retrievedCar);

        verify(carRepository).findByLicencePlate(licencePlate);
    }

    @Test
    void testGetCarNonexistent() {
        String licencePlate = PODAM_FACTORY.manufacturePojo(String.class);
        when(carRepository.findByLicencePlate(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carBusinessService.getCar(licencePlate))
                .isInstanceOf(NoSuchCarException.class)
                .hasMessage("This car does not exist in our system: " + licencePlate + ".");

        verify(carRepository).findByLicencePlate(licencePlate);
    }

    @Test
    void testCreateCar() {
        Car car = PODAM_FACTORY.manufacturePojo(Car.class);
        when(carRepository.save(any())).thenReturn(car);

        Car createdCar = carBusinessService.createCar(car);

        assertThat(car).isEqualTo(createdCar);

        verify(carRepository).save(car);
    }
}