package com.ferenc.reservation.businessservice;

import static com.ferenc.reservation.TestConstants.END_DATE;
import static com.ferenc.reservation.TestConstants.LICENCE_PLATE;
import static com.ferenc.reservation.TestConstants.LICENCE_PLATE_OTHER;
import static com.ferenc.reservation.TestConstants.START_DATE;
import static com.ferenc.reservation.TestConstants.USER_ID;
import static com.ferenc.reservation.TestConstants.USER_ID_OTHER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ferenc.reservation.AbstractTest;
import com.ferenc.reservation.amqp.service.BookingEventPublishingService;
import com.ferenc.reservation.controller.dto.BookingRequest;
import com.ferenc.reservation.controller.dto.UpdateRequest;
import com.ferenc.reservation.exception.CarNotAvailableException;
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

    private static Stream<Arguments> values_isCarAvailable_false() {
        return Stream.of(
                Arguments.of(
                        START_DATE.minusDays(1), START_DATE,
                        END_DATE, END_DATE.plusDays(1),
                        START_DATE.minusDays(1), END_DATE.plusDays(1)
                )
        );
    }

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
        assertEquals(expected, actual);

        verify(carRepository).findByLicencePlate(car.getLicencePlate());
        verify(bookingRepository).findByCarLicencePlate(car.getLicencePlate());
        verify(lockService).acquireLock(car.getLicencePlate(), expected.getUserId());
        verify(bookingSequenceHelper).getNextSequence();
        verify(lockService).releaseLock(car.getLicencePlate(), expected.getUserId());

        ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(captor.capture());
        assertEquals(expected, captor.getValue());

        ArgumentCaptor<Booking> eventCaptor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingEventPublishingService).publishNewBookingEvent(eventCaptor.capture());
        assertEquals(expected, eventCaptor.getValue());
    }

    @Test
    void createBooking_carNotAvailable() {
        BookingRequest bookingRequest = getValidBookingRequest();
        Car car = PODAM_FACTORY.manufacturePojo(Car.class);

        Booking expected = new Booking();
        expected.setCar(car);
        expected.setUserId(PODAM_FACTORY.manufacturePojo(String.class));
        expected.setStartDate(bookingRequest.getDateRange().getStartDate());
        expected.setEndDate(bookingRequest.getDateRange().getEndDate());
        expected.setBookingId(PODAM_FACTORY.manufacturePojo(Integer.class));

        when(carRepository.findByLicencePlate(any())).thenReturn(Optional.of(car));
        when(bookingRepository.findByCarLicencePlate(any())).thenReturn(List.of(expected));
        when(lockService.acquireLock(any(), any())).thenReturn(true);

        assertThatThrownBy(() -> bookingBusinessService.createBooking(expected.getUserId(), car.getLicencePlate(), expected.getStartDate(),
                expected.getEndDate())).isInstanceOf(CarNotAvailableException.class)
                .hasMessage(String.format("This car: %s is not available for this time range: %s - %s.",
                        car.getLicencePlate(), expected.getStartDate(), expected.getEndDate()));

        verify(carRepository).findByLicencePlate(car.getLicencePlate());
        verify(bookingRepository).findByCarLicencePlate(car.getLicencePlate());
        verify(lockService).acquireLock(car.getLicencePlate(), expected.getUserId());
        verify(lockService).releaseLock(car.getLicencePlate(), expected.getUserId());
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

        List<Booking> existingBookings = new ArrayList<>();
        existingBookings.add(expected);

        when(bookingRepository.findByBookingId(any())).thenReturn(Optional.of(expected));
        when(bookingRepository.findByCarLicencePlate(any())).thenReturn(existingBookings);
        when(lockService.acquireLock(any(), any())).thenReturn(true);

        Booking actual = bookingBusinessService.updateBooking(expected.getBookingId(), expected.getStartDate(), expected.getEndDate());
        assertEquals(expected, actual);

        verify(bookingRepository).findByBookingId(expected.getBookingId());
        verify(bookingRepository).findByCarLicencePlate(car.getLicencePlate());
        verify(lockService).acquireLock(car.getLicencePlate(), expected.getUserId());
        verify(lockService).releaseLock(car.getLicencePlate(), expected.getUserId());

        ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(captor.capture());
        assertEquals(expected, captor.getValue());
    }

    @Test
    void updateBooking_carNotAvailable() {
        BookingRequest bookingRequest = getValidBookingRequest();
        Car car = PODAM_FACTORY.manufacturePojo(Car.class);

        Booking expected = new Booking();
        expected.setCar(car);
        expected.setUserId(USER_ID);
        expected.setStartDate(bookingRequest.getDateRange().getStartDate());
        expected.setEndDate(bookingRequest.getDateRange().getEndDate());
        expected.setBookingId(PODAM_FACTORY.manufacturePojo(Integer.class));

        Booking existing = new Booking();
        existing.setCar(expected.getCar());
        existing.setUserId(USER_ID_OTHER);
        existing.setStartDate(expected.getStartDate());
        existing.setEndDate(expected.getEndDate());
        existing.setBookingId(PODAM_FACTORY.manufacturePojo(Integer.class));

        List<Booking> existingBookings = new ArrayList<>();
        existingBookings.add(existing);

        when(bookingRepository.findByBookingId(any())).thenReturn(Optional.of(expected));
        when(bookingRepository.findByCarLicencePlate(any())).thenReturn(existingBookings);
        when(lockService.acquireLock(any(), any())).thenReturn(true);

        assertThatThrownBy(() -> bookingBusinessService.updateBooking(expected.getBookingId(), expected.getStartDate(),
                expected.getEndDate())).isInstanceOf(CarNotAvailableException.class)
                .hasMessage(String.format("This car: %s is not available for this time range: %s - %s.",
                        car.getLicencePlate(), existing.getStartDate(), existing.getEndDate()));

        verify(bookingRepository).findByBookingId(expected.getBookingId());
        verify(bookingRepository).findByCarLicencePlate(car.getLicencePlate());
        verify(lockService).acquireLock(car.getLicencePlate(), expected.getUserId());
        verify(lockService).releaseLock(car.getLicencePlate(), expected.getUserId());
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
        assertEquals(expected, actual);

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

        String userId = booking.getUserId();
        List<Booking> expected = List.of(booking);

        when(bookingRepository.findByUserId(any())).thenReturn(expected);

        List<Booking> actual = bookingBusinessService.getAllBookingsByUserId(userId);
        assertThat(expected).containsAll(actual);

        verify(bookingRepository).findByUserId(userId);
    }

    @Test
    void deleteBooking() {
        BookingRequest bookingRequest = getValidBookingRequest();
        Car car = PODAM_FACTORY.manufacturePojo(Car.class);

        Booking expected = new Booking();
        expected.setCar(car);
        expected.setUserId(PODAM_FACTORY.manufacturePojo(String.class));
        expected.setStartDate(bookingRequest.getDateRange().getStartDate());
        expected.setEndDate(bookingRequest.getDateRange().getEndDate());
        expected.setBookingId(PODAM_FACTORY.manufacturePojo(Integer.class));

        when(bookingRepository.findByBookingId(any())).thenReturn(Optional.of(expected));
        when(lockService.acquireLock(any(), any())).thenReturn(true);

        Booking actual = bookingBusinessService.deleteBooking(expected.getBookingId());
        assertEquals(expected, actual);

        verify(bookingRepository).findByBookingId(expected.getBookingId());
        verify(bookingRepository).deleteById(expected.getBookingId());
        verify(lockService).acquireLock(car.getLicencePlate(), expected.getUserId());
        verify(lockService).releaseLock(car.getLicencePlate(), expected.getUserId());
    }

    @Test
    void isCarAvailable_true() {
        Car car = PODAM_FACTORY.manufacturePojo(Car.class);
        car.setLicencePlate(LICENCE_PLATE);

        Booking existing = new Booking();
        existing.setCar(car);
        existing.setUserId(USER_ID_OTHER);
        existing.setStartDate(START_DATE);
        existing.setEndDate(END_DATE);
        existing.setBookingId(PODAM_FACTORY.manufacturePojo(Integer.class));

        List<Booking> existingBookings = new ArrayList<>();
        existingBookings.add(existing);

        when(bookingRepository.findByCarLicencePlate(LICENCE_PLATE)).thenReturn(existingBookings);
        when(bookingRepository.findByCarLicencePlate(LICENCE_PLATE_OTHER)).thenReturn(List.of());

        assertThat(bookingBusinessService.isCarAvailable(LICENCE_PLATE,
                END_DATE.plusDays(1), END_DATE.plusDays(1))).isTrue();

        assertThat(bookingBusinessService.isCarAvailable(LICENCE_PLATE_OTHER,
                START_DATE, END_DATE)).isTrue();

        verify(bookingRepository).findByCarLicencePlate(LICENCE_PLATE);
        verify(bookingRepository).findByCarLicencePlate(LICENCE_PLATE_OTHER);
    }

    @ParameterizedTest
    @MethodSource("values_isCarAvailable_false")
    void isCarAvailable_false(LocalDate startDate, LocalDate endDate) {
        Car car = PODAM_FACTORY.manufacturePojo(Car.class);
        car.setLicencePlate(LICENCE_PLATE);

        Booking existing = new Booking();
        existing.setCar(car);
        existing.setUserId(USER_ID_OTHER);
        existing.setStartDate(startDate);
        existing.setEndDate(endDate);
        existing.setBookingId(PODAM_FACTORY.manufacturePojo(Integer.class));

        List<Booking> existingBookings = new ArrayList<>();
        existingBookings.add(existing);

        when(bookingRepository.findByCarLicencePlate(any())).thenReturn(existingBookings);

        assertThat(bookingBusinessService.isCarAvailable(LICENCE_PLATE,
                startDate, endDate)).isFalse();

        verify(bookingRepository).findByCarLicencePlate(LICENCE_PLATE);
    }
}
