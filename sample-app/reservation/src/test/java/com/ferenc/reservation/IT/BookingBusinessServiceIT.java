package com.ferenc.reservation.IT;

import static com.ferenc.reservation.TestConstants.BOOKING_ID;
import static com.ferenc.reservation.TestConstants.BOOKING_ID_OTHER;
import static com.ferenc.reservation.TestConstants.END_DATE;
import static com.ferenc.reservation.TestConstants.INITIAL_SEQUENCE;
import static com.ferenc.reservation.TestConstants.LICENCE_PLATE_OTHER;
import static com.ferenc.reservation.TestConstants.START_DATE;
import static com.ferenc.reservation.TestConstants.USER_ID;
import static com.ferenc.reservation.TestConstants.USER_ID_OTHER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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

@SpringBootTest
@ActiveProfiles("test")
@Tag("IntegrationTest")
class BookingBusinessServiceIT extends AbstractTest {

    @Autowired
    AmqpConfiguration amqpConfiguration;
    @Autowired
    BookingBusinessService bookingBusinessService;
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
    @MockBean
    private RabbitTemplate rabbitTemplate;

    @BeforeEach
    void init() {
        bookingSequenceRepository.save(new BookingSequence(INITIAL_SEQUENCE, bookingSequenceHelper.getBookingSequenceKey()));
        Set<Car> cars = getCars();
        carRepository.saveAll(cars);
        Car car = cars.stream().filter(c -> c.getLicencePlate().equals(LICENCE_PLATE_OTHER)).findFirst().get();
        Booking booking = new Booking(bookingSequenceHelper.getNextSequence(), USER_ID, START_DATE, END_DATE, car);
        bookingRepository.save(booking);
    }

    @AfterEach
    void cleanUp() {
        bookingSequenceRepository.deleteAll();
        bookingRepository.deleteAll();
        carRepository.deleteAll();
    }

    @Test
    void testCreateBooking() {
        BookingRequest bookingRequest = getValidBookingRequest();
        Booking booking = bookingBusinessService
                .createBooking(
                        USER_ID,
                        bookingRequest.getLicencePlate(),
                        bookingRequest.getDateRange().getStartDate(),
                        bookingRequest.getDateRange().getEndDate()
                );

        assertEquals(USER_ID, booking.getUserId());
        assertEquals(booking.getCar().getLicencePlate(), bookingRequest.getLicencePlate());
        assertEquals(booking.getStartDate(), bookingRequest.getDateRange().getStartDate());
        assertEquals(booking.getEndDate(), bookingRequest.getDateRange().getEndDate());

        assertThat(bookingRepository.findByUserId(USER_ID)).contains(booking);

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
                eq(amqpConfiguration.getBookingRoutingKey()), eventCaptor.capture());
        assertThat(eventCaptor.getValue())
                .usingRecursiveComparison()
                .ignoringFields("timeCreated")
                .isEqualTo(bookingEvent);
    }

    @Test
    void testCreateBooking_ForCarNotAvailableException() {
        BookingRequest bookingRequest = getValidBookingRequest();
        bookingBusinessService
                .createBooking(
                        USER_ID,
                        bookingRequest.getLicencePlate(),
                        bookingRequest.getDateRange().getStartDate(),
                        bookingRequest.getDateRange().getEndDate()
                );

        assertThrows(CarNotAvailableException.class,
                () -> bookingBusinessService
                        .createBooking(
                                USER_ID,
                                bookingRequest.getLicencePlate(),
                                bookingRequest.getDateRange().getStartDate(),
                                bookingRequest.getDateRange().getEndDate()));
    }

    @Test
    void testGetBooking() {
        Booking booking = bookingBusinessService.getBooking(BOOKING_ID);
        assertEquals(BOOKING_ID, booking.getBookingId());
        assertThat(bookingRepository.findByUserId(USER_ID)).contains(booking);
    }

    @Test
    void testGetBookin_ForNoSuchBookingException() {
        assertThrows(NoSuchBookingException.class,
                () -> bookingBusinessService.getBooking(BOOKING_ID_OTHER));
    }

    @Test
    void testGetAllBookings() {
        BookingRequest bookingRequest = getValidBookingRequest();
        Booking booking = bookingBusinessService
                .createBooking(
                        USER_ID,
                        bookingRequest.getLicencePlate(),
                        bookingRequest.getDateRange().getStartDate(),
                        bookingRequest.getDateRange().getEndDate()
                );
        List<Booking> bookings = bookingBusinessService.getAllBookings();

        assertEquals(2, bookings.size());
        assertTrue(bookings.contains(booking));
        assertThat(bookingRepository.findByUserId(USER_ID)).contains(booking);
    }

    @Test
    void testUpdateBooking() {
        UpdateRequest updateRequest = getValidUpdateRequest();
        Booking booking = bookingBusinessService
                .updateBooking(
                        BOOKING_ID,
                        updateRequest.getDateRange().getStartDate(),
                        updateRequest.getDateRange().getEndDate());

        assertEquals(updateRequest.getDateRange().getStartDate(), booking.getStartDate());
        assertEquals(updateRequest.getDateRange().getEndDate(), booking.getEndDate());
        assertThat(bookingRepository.findByUserId(USER_ID)).contains(booking);
    }

    @Test
    void testUpdateBooking_ForCarNotAvailableException() {
        BookingRequest bookingRequest = getValidBookingRequest();
        Booking booking1 = bookingBusinessService
                .createBooking(
                        USER_ID_OTHER,
                        bookingRequest.getLicencePlate(),
                        bookingRequest.getDateRange().getStartDate(),
                        bookingRequest.getDateRange().getEndDate()
                );
        Booking booking2 = bookingBusinessService
                .createBooking(
                        USER_ID,
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
        bookingBusinessService.deleteBooking(BOOKING_ID);

        assertThrows(NoSuchBookingException.class, () -> bookingBusinessService.getBooking(BOOKING_ID));
        assertThat(bookingRepository.findByBookingId(BOOKING_ID)).isEmpty();
    }
}
