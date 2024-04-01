package com.ferenc.reservation.it;

import static com.ferenc.reservation.TestConstants.END_DATE;
import static com.ferenc.reservation.TestConstants.INITIAL_SEQUENCE;
import static com.ferenc.reservation.TestConstants.LICENCE_PLATE;
import static com.ferenc.reservation.TestConstants.START_DATE;
import static com.ferenc.reservation.TestConstants.USER_ID;

import java.util.Set;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.ferenc.reservation.repository.PessimisticLockRepository;
import com.ferenc.reservation.repository.lock.LockService;
import com.ferenc.reservation.repository.model.Booking;
import com.ferenc.reservation.repository.model.BookingSequence;
import com.ferenc.reservation.repository.model.Car;

@SpringBootTest
@ActiveProfiles("test")
@Tag("IntegrationTest")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VirtualThreadsTest extends AbstractTest {

    private static final int SIZE = 100_000;
    public static final int TIMEOUT = SIZE / 100;
    public static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

    private final Logger logger = LoggerFactory.getLogger(VirtualThreadsTest.class);

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
    @MockBean
    private LockService lockService;
    @MockBean
    private PessimisticLockRepository pessimisticLockRepository;
    @MockBean
    private BookingEventPublishingService bookingEventPublishingService;

    @BeforeEach
    void init() {
        bookingSequenceRepository.save(new BookingSequence(INITIAL_SEQUENCE, bookingSequenceHelper.getBookingSequenceKey()));
        Set<Car> cars = getCars();
        carRepository.saveAll(cars);
        Car car = cars.stream().filter(c -> c.getLicencePlate().equals(LICENCE_PLATE)).findFirst().get();
        Booking booking = new Booking(bookingSequenceHelper.getNextSequence(), USER_ID, START_DATE, END_DATE, car);
        bookingRepository.save(booking);
    }

    @AfterEach
    void cleanUp() {
        bookingRepository.deleteAll();
        bookingSequenceRepository.deleteAll();
        carRepository.deleteAll();
    }

    @Test
    @Order(1)
    void testConcurrentBooking_virtual() throws InterruptedException {
        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
        long startTime = System.nanoTime();
        int i = 0;
        try {
            while (i++ < SIZE) {
                executorService.submit(() -> bookingService.getAllBookings());
            }
        }catch (Throwable e){
            logger.error("Error: {}, cause: {} at {}." , e.getMessage(), e.getCause(), i);
        } finally {
            executorService.shutdown();
            executorService.awaitTermination(TIMEOUT, TIME_UNIT);
        }
        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        logger.info("\n\n Time taken on virtual pool to perform {} operations was {} nanoseconds. \n", SIZE, elapsedTime);
    }

    @Test
    @Order(2)
    void testConcurrentBooking_normal() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(SIZE);
        long startTime = System.nanoTime();
        int i = 0;
        try {
            while (i++ < SIZE) {
                executorService.submit(() -> bookingService.getAllBookings());
            }
        }catch (Throwable e){
            logger.error("Error: {}, cause: {} at {}." , e.getMessage(), e.getCause(), i);
        } finally {
            executorService.shutdown();
            executorService.awaitTermination(TIMEOUT, TIME_UNIT);
        }
        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        logger.info("\n\n Time taken on normal pool to perform {} operations was {} nanoseconds. \n", SIZE, elapsedTime);
    }
}
