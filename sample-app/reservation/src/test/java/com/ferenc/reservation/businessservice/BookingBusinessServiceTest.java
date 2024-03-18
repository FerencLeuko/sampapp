package com.ferenc.reservation.businessservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ferenc.reservation.AbstractTest;
import com.ferenc.reservation.amqp.service.BookingEventPublishingService;
import com.ferenc.reservation.controller.dto.BookingRequest;
import com.ferenc.reservation.controller.dto.UpdateRequest;
import com.ferenc.reservation.repository.BookingRepository;
import com.ferenc.reservation.repository.BookingSequenceHelper;
import com.ferenc.reservation.repository.CarRepository;
import com.ferenc.reservation.repository.lock.LockService;
import com.ferenc.reservation.repository.model.Booking;
import com.ferenc.reservation.repository.model.Car;

@ExtendWith(MockitoExtension.class)
class BookingBusinessServiceTest extends AbstractTest {

    @Mock
    private CarRepository carRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingSequenceHelper bookingSequenceHelper;
    @Mock
    private BookingEventPublishingService bookingEventPublishingService;
    @Mock
    private LockService lockService;
    @InjectMocks
    private BookingBusinessServiceImpl bookingBusinessService;

    @AfterEach
    void verifyMocks() {
        verifyNoMoreInteractions(carRepository, bookingRepository, bookingSequenceHelper, bookingEventPublishingService, lockService);
    }

    @Test
    void createBooking() {
        BookingRequest bookingRequest = getValidBookingRequest();
        Car car = PODAM_FACTORY.manufacturePojo(Car.class);

        Booking expected = new Booking();
        expected.setCar(car);
        expected.setUserId(PODAM_FACTORY.manufacturePojo(String.class));
        expected.setStartDate(bookingRequest.getDateRange().getStartDate());
        expected.setEndDate(bookingRequest.getDateRange().getEndDate());
        expected.setBookingId(PODAM_FACTORY.manufacturePojo(Integer.class));

        when(carRepository.findByLicencePlate(any())).thenReturn(Optional.of(car));
        when(bookingRepository.findByCarLicencePlate(any())).thenReturn(List.of());
        when(lockService.acquireLock(any(), any())).thenReturn(true);
        when(bookingSequenceHelper.getNextSequence()).thenReturn(expected.getBookingId());

        Booking actual = bookingBusinessService.createBooking(expected.getUserId(), car.getLicencePlate(), expected.getStartDate(),
                expected.getEndDate());
        assertEquals(actual, expected);

        verify(carRepository).findByLicencePlate(car.getLicencePlate());
        verify(bookingRepository).findByCarLicencePlate(car.getLicencePlate());
        verify(lockService).acquireLock(car.getLicencePlate(), expected.getUserId());
        verify(bookingSequenceHelper).getNextSequence();
        verify(lockService).releaseLock(car.getLicencePlate(), expected.getUserId());

        ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(captor.capture());
        assertEquals(captor.getValue(), expected);

        ArgumentCaptor<Booking> eventCaptor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingEventPublishingService).publishNewBookingEvent(eventCaptor.capture());
        assertEquals(eventCaptor.getValue(), expected);
    }

    @Test
    void updateBooking() {
        UpdateRequest updateRequest = getValidUpdateRequest();
        Car car = PODAM_FACTORY.manufacturePojo(Car.class);

        Booking expected = new Booking();
        expected.setCar(car);
        expected.setUserId(PODAM_FACTORY.manufacturePojo(String.class));
        expected.setStartDate(updateRequest.getDateRange().getStartDate());
        expected.setEndDate(updateRequest.getDateRange().getEndDate());
        expected.setBookingId(PODAM_FACTORY.manufacturePojo(Integer.class));

        when(bookingRepository.findByBookingId(any())).thenReturn(Optional.of(expected));
        when(bookingRepository.findByCarLicencePlate(any())).thenReturn(new ArrayList<>());
        when(lockService.acquireLock(any(), any())).thenReturn(true);

        Booking actual = bookingBusinessService.updateBooking(expected.getBookingId(), expected.getStartDate(), expected.getEndDate());
        assertEquals(actual, expected);

        verify(bookingRepository).findByBookingId(expected.getBookingId());
        verify(bookingRepository).findByCarLicencePlate(car.getLicencePlate());
        verify(lockService).acquireLock(car.getLicencePlate(), expected.getUserId());
        verify(lockService).releaseLock(car.getLicencePlate(), expected.getUserId());

        ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(captor.capture());
        assertEquals(captor.getValue(), expected);
    }

    @Test
    void getBooking() {
        BookingRequest bookingRequest = getValidBookingRequest();
        Car car = PODAM_FACTORY.manufacturePojo(Car.class);

        Booking expected = new Booking();
        expected.setCar(car);
        expected.setUserId(PODAM_FACTORY.manufacturePojo(String.class));
        expected.setStartDate(bookingRequest.getDateRange().getStartDate());
        expected.setEndDate(bookingRequest.getDateRange().getEndDate());
        expected.setBookingId(PODAM_FACTORY.manufacturePojo(Integer.class));

        when(bookingRepository.findByBookingId(any())).thenReturn(Optional.of(expected));

        Booking actual = bookingBusinessService.getBooking(expected.getBookingId());
        assertEquals(actual, expected);

        verify(bookingRepository).findByBookingId(expected.getBookingId());
    }

    @Test
    void getAllBookings() {
        bookingBusinessService.getAllBookings();
        verify(bookingRepository).findAll();
    }

    @Test
    void getAllBookingsByUserId() {
        BookingRequest bookingRequest = getValidBookingRequest();
        Car car = PODAM_FACTORY.manufacturePojo(Car.class);

        Booking booking = new Booking();
        booking.setCar(car);
        booking.setUserId(PODAM_FACTORY.manufacturePojo(String.class));
        booking.setStartDate(bookingRequest.getDateRange().getStartDate());
        booking.setEndDate(bookingRequest.getDateRange().getEndDate());
        booking.setBookingId(PODAM_FACTORY.manufacturePojo(Integer.class));

        List<Booking> expected = List.of(booking);

        when(bookingRepository.findByUserId(any())).thenReturn(expected);

        List<Booking> actual = bookingBusinessService.getAllBookingsByUserId(booking.getUserId());
        assertThat(actual).containsAll(expected);

        verify(bookingRepository).findByUserId(booking.getUserId());
    }

    @Test
    void deleteBooking() {
        BookingRequest bookingRequest = getValidBookingRequest();
        Car car = PODAM_FACTORY.manufacturePojo(Car.class);

        Booking booking = new Booking();
        booking.setCar(car);
        booking.setUserId(PODAM_FACTORY.manufacturePojo(String.class));
        booking.setStartDate(bookingRequest.getDateRange().getStartDate());
        booking.setEndDate(bookingRequest.getDateRange().getEndDate());
        booking.setBookingId(PODAM_FACTORY.manufacturePojo(Integer.class));

        when(bookingRepository.findByBookingId(any())).thenReturn(Optional.of(booking));

        bookingBusinessService.deleteBooking(booking.getBookingId());

        verify(bookingRepository).findByBookingId(booking.getBookingId());
        verify(bookingRepository).deleteById(booking.getBookingId());
    }
}
