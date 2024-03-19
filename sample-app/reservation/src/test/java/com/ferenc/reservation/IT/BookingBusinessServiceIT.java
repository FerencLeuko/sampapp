package com.ferenc.reservation.IT;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.ferenc.commons.event.BookingEvent;
import com.ferenc.reservation.AbstractTest;
import com.ferenc.reservation.amqp.AmqpConfiguration;
import com.ferenc.reservation.amqp.service.BookingEventPublishingService;
import com.ferenc.reservation.businessservice.BookingBusinessService;
import com.ferenc.reservation.businessservice.BookingBusinessServiceImpl;
import com.ferenc.reservation.controller.dto.BookingRequest;
import com.ferenc.reservation.controller.dto.UpdateRequest;
import com.ferenc.reservation.exception.CarNotAvailableException;
import com.ferenc.reservation.exception.NoSuchBookingException;
import com.ferenc.reservation.repository.BookingRepository;
import com.ferenc.reservation.repository.BookingSequenceHelper;
import com.ferenc.reservation.repository.BookingSequenceRepository;
import com.ferenc.reservation.repository.CarRepository;
import com.ferenc.reservation.repository.lock.LockService;
import com.ferenc.reservation.repository.model.Booking;
import com.ferenc.reservation.repository.model.BookingSequence;
import com.ferenc.reservation.repository.model.Car;
import com.ferenc.reservation.repository.model.CarTypeEnum;

@SpringBootTest
@ActiveProfiles("test")
@Tag("IntegrationTest")
class BookingBusinessServiceIT extends AbstractTest {

    @Autowired
    private CarRepository carRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private BookingSequenceHelper bookingSequenceHelper;
    @Autowired
    private BookingSequenceRepository bookingSequenceRepository;
    @Autowired
    private LockService lockService;
    @Autowired
    private BookingEventPublishingService bookingEventPublishingService;
    @Autowired
    AmqpConfiguration amqpConfiguration;
    @MockBean
    private RabbitTemplate rabbitTemplate;

    @BeforeEach
    void init() {
        BookingSequence initialBookingSequence = new BookingSequence(0, bookingSequenceHelper.getBookingSequenceKey());
        bookingSequenceRepository.save(initialBookingSequence);
        Car car1 = new Car(TEST_LICENCE_PLATE, "Opel", "Astra", CarTypeEnum.SEDAN, 5);
        carRepository.save(car1);
        Car car2 = new Car("ABC124", "Opel", "Astra", CarTypeEnum.SEDAN, 5);
        carRepository.save(car2);
        Car car3 = new Car("ABC125", "Opel", "Astra", CarTypeEnum.STATION_WAGON, 5);
        carRepository.save(car3);
        Booking booking1 = new Booking(bookingSequenceHelper.getNextSequence(), TEST_USER_ID, LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(10), car3);
        bookingRepository.save(booking1);
    }

    @AfterEach
    void cleanUp() {
        bookingSequenceRepository.deleteAll();
        bookingRepository.deleteAll();
        carRepository.deleteAll();
    }

    @Test
    void testCreateBooking() {
        BookingBusinessService bookingBusinessService = getBookingBusinessService();
        BookingRequest bookingRequest = getValidBookingRequest();
        Booking booking = bookingBusinessService
                .createBooking(
                        TEST_USER_ID,
                        bookingRequest.getLicencePlate(),
                        bookingRequest.getDateRange().getStartDate(),
                        bookingRequest.getDateRange().getEndDate()
                );

        assertEquals(TEST_USER_ID, booking.getUserId());
        assertEquals(booking.getCar().getLicencePlate(), bookingRequest.getLicencePlate());
        assertEquals(booking.getStartDate(), bookingRequest.getDateRange().getStartDate());
        assertEquals(booking.getEndDate(), bookingRequest.getDateRange().getEndDate());

        assertThat(bookingRepository.findByUserId(TEST_USER_ID)).contains(booking);

        BookingEvent bookingEvent =
                BookingEvent.builder()
                        .bookingId(booking.getBookingId())
                        .timeCreated(LocalDateTime.now())
                        .userId(booking.getUserId())
                        .lisencePlate(booking.getCar().getLicencePlate())
                        .startDate(booking.getStartDate())
                        .endDate(booking.getEndDate())
                        .build();

        ArgumentCaptor<BookingEvent> eventCaptor = ArgumentCaptor.forClass(BookingEvent.class);
        verify(rabbitTemplate).convertAndSend(eq(amqpConfiguration.getBookingExchangeName()),
                eq(amqpConfiguration.getBookingRoutingKey()),eventCaptor.capture());
        assertThat(eventCaptor.getValue())
                .usingRecursiveComparison()
                .ignoringFields("timeCreated")
                .isEqualTo(bookingEvent);
    }

    @Test
    void testCreateBooking_ForCarNotAvailableException() {
        BookingBusinessService bookingBusinessService = getBookingBusinessService();
        BookingRequest bookingRequest = getValidBookingRequest();
        bookingBusinessService
                .createBooking(
                        TEST_USER_ID,
                        bookingRequest.getLicencePlate(),
                        bookingRequest.getDateRange().getStartDate(),
                        bookingRequest.getDateRange().getEndDate()
                );

        assertThrows(CarNotAvailableException.class,
                () -> bookingBusinessService
                        .createBooking(
                                TEST_USER_ID,
                                bookingRequest.getLicencePlate(),
                                bookingRequest.getDateRange().getStartDate(),
                                bookingRequest.getDateRange().getEndDate()));
    }

    @Test
    void testGetBooking() {
        BookingBusinessService bookingBusinessService = getBookingBusinessService();
        Booking booking = bookingBusinessService.getBooking(TEST_BOOKING_ID);
        assertEquals(TEST_BOOKING_ID, booking.getBookingId());
        assertThat(bookingRepository.findByUserId(TEST_USER_ID)).contains(booking);
    }

    @Test
    void testGetBookin_ForNoSuchBookingException() {
        BookingBusinessService bookingBusinessService = getBookingBusinessService();
        assertThrows(NoSuchBookingException.class,
                () -> bookingBusinessService.getBooking(TEST_BOOKING_ID + 1));
    }

    @Test
    void testGetAllBookings() {
        BookingBusinessService bookingBusinessService = getBookingBusinessService();
        BookingRequest bookingRequest = getValidBookingRequest();
        Booking booking = bookingBusinessService
                .createBooking(
                        TEST_USER_ID,
                        bookingRequest.getLicencePlate(),
                        bookingRequest.getDateRange().getStartDate(),
                        bookingRequest.getDateRange().getEndDate()
                );
        List<Booking> bookings = bookingBusinessService.getAllBookings();

        assertEquals(2, bookings.size());
        assertTrue(bookings.contains(booking));
        assertThat(bookingRepository.findByUserId(TEST_USER_ID)).contains(booking);
    }

    @Test
    void testUpdateBooking() {
        BookingBusinessService bookingBusinessService = getBookingBusinessService();
        UpdateRequest updateRequest = getValidUpdateRequest();
        Booking booking = bookingBusinessService
                .updateBooking(
                        TEST_BOOKING_ID,
                        updateRequest.getDateRange().getStartDate(),
                        updateRequest.getDateRange().getEndDate());

        assertEquals(updateRequest.getDateRange().getStartDate(), booking.getStartDate());
        assertEquals(updateRequest.getDateRange().getEndDate(), booking.getEndDate());
        assertThat(bookingRepository.findByUserId(TEST_USER_ID)).contains(booking);
    }

    @Test
    void testUpdateBooking_ForCarNotAvailableException() {
        BookingBusinessService bookingBusinessService = getBookingBusinessService();
        BookingRequest bookingRequest = getValidBookingRequest();
        Booking booking1 = bookingBusinessService
                .createBooking(
                        "foo" + TEST_USER_ID,
                        bookingRequest.getLicencePlate(),
                        bookingRequest.getDateRange().getStartDate(),
                        bookingRequest.getDateRange().getEndDate()
                );
        Booking booking2 = bookingBusinessService
                .createBooking(
                        TEST_USER_ID,
                        bookingRequest.getLicencePlate(),
                        bookingRequest.getDateRange().getEndDate().plusDays(1),
                        bookingRequest.getDateRange().getEndDate().plusDays(1)
                );

        assertThrows(CarNotAvailableException.class,
                () -> bookingBusinessService
                        .updateBooking(
                                booking2.getBookingId(),
                                booking1.getStartDate(),
                                booking1.getEndDate()));
    }

    @Test
    void testDeleteBooking() {
        BookingBusinessService bookingBusinessService = getBookingBusinessService();
        bookingBusinessService.deleteBooking(TEST_BOOKING_ID);

        assertThrows(NoSuchBookingException.class, () -> bookingBusinessService.getBooking(TEST_BOOKING_ID));
        assertThat(bookingRepository.findByBookingId(TEST_BOOKING_ID)).isEmpty();
    }

    private BookingBusinessService getBookingBusinessService() {
        BookingBusinessService bookingBusinessService =
                new BookingBusinessServiceImpl(carRepository, bookingRepository, bookingSequenceHelper, bookingEventPublishingService, lockService);
        return bookingBusinessService;
    }

}
