package com.ferenc.reservation.IT;

import static com.ferenc.reservation.TestConstants.END_DATE;
import static com.ferenc.reservation.TestConstants.INITIAL_SEQUENCE;
import static com.ferenc.reservation.TestConstants.LICENCE_PLATE;
import static com.ferenc.reservation.TestConstants.START_DATE;
import static com.ferenc.reservation.TestConstants.USER_ID;
import static com.ferenc.reservation.TestConstants.USER_ID_OTHER;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.ferenc.reservation.AbstractTest;
import com.ferenc.reservation.amqp.service.BookingEventPublishingService;
import com.ferenc.reservation.businessservice.BookingBusinessService;
import com.ferenc.reservation.repository.BookingRepository;
import com.ferenc.reservation.repository.BookingSequenceHelper;
import com.ferenc.reservation.repository.BookingSequenceRepository;
import com.ferenc.reservation.repository.CarRepository;
import com.ferenc.reservation.repository.lock.LockService;
import com.ferenc.reservation.repository.model.Booking;
import com.ferenc.reservation.repository.model.BookingSequence;

@SpringBootTest
@ActiveProfiles("test")
@Tag("IntegrationTest")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LockServiceIT extends AbstractTest {

    @Autowired
    private BookingBusinessService bookingService;
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
    @MockBean
    private BookingEventPublishingService bookingEventPublishingService;

    @BeforeEach
    void init() {
        bookingSequenceRepository.save(new BookingSequence(INITIAL_SEQUENCE, bookingSequenceHelper.getBookingSequenceKey()));
        carRepository.saveAll(getCars());
    }

    @AfterEach
    void cleanUp() {
        bookingRepository.deleteAll();
        bookingSequenceRepository.deleteAll();
        carRepository.deleteAll();
    }

    @Test
    @Order(1)
    void testConcurrentBooking() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        try {
            executorService.submit(() -> bookingService.createBooking(USER_ID, LICENCE_PLATE, START_DATE, END_DATE));
            executorService.submit(() -> bookingService.createBooking(USER_ID_OTHER, LICENCE_PLATE, END_DATE.plusDays(3), END_DATE.plusDays(3)));
        } finally {
            executorService.shutdown();
        }
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        List<Booking> bookings = bookingRepository.findByCarLicencePlate(LICENCE_PLATE);

        assertThat(bookings)
                .hasSize(2)
                .anySatisfy(booking -> assertThat(booking.getUserId()).isEqualTo(USER_ID))
                .anySatisfy(booking -> assertThat(booking.getUserId()).isEqualTo(USER_ID_OTHER));
    }

    @Test
    @Order(2)
    void testConcurrentBookingSameDates() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        try {
            executorService.submit(() -> bookingService.createBooking(USER_ID, LICENCE_PLATE, START_DATE, END_DATE));
            executorService.submit(() -> bookingService.createBooking(USER_ID_OTHER, LICENCE_PLATE, START_DATE, END_DATE));
        } catch (Exception e) {
        } finally {
            executorService.shutdown();
        }
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        assertThat(bookingRepository.findByCarLicencePlate(LICENCE_PLATE)).hasSize(1);
    }
}