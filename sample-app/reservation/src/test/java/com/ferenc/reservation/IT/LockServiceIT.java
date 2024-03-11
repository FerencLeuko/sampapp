package com.ferenc.reservation.IT;

import com.ferenc.reservation.amqp.service.BookingEventPublishingService;
import com.ferenc.reservation.businessservice.BookingBusinessService;
import com.ferenc.reservation.repository.*;
import com.ferenc.reservation.repository.lock.LockService;
import com.ferenc.reservation.repository.model.Booking;
import com.ferenc.reservation.repository.model.BookingSequence;
import com.ferenc.reservation.repository.model.Car;
import com.ferenc.reservation.repository.model.CarTypeEnum;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Tag("IntegrationTest")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LockServiceIT {

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
    @Autowired
    private PessimisticLockRepository pessimisticLockRepository;

    @Mock
    private BookingEventPublishingService bookingEventPublishingService;

    private final String userId1 = "user1";
    private final String userId2 = "user2";
    private final String licencePlate = "ABC123";
    private final LocalDate startDate = LocalDate.now();
    private final LocalDate endDate = startDate.plusDays(1);

    @BeforeEach
    void init() {
        BookingSequence initialBookingSequence = new BookingSequence(0, bookingSequenceHelper.getBookingSequenceKey());
        bookingSequenceRepository.save(initialBookingSequence);
        Car car1 = new Car("ABC123","Opel","Astra", CarTypeEnum.SEDAN,5);
        carRepository.save(car1);
        Car car2 = new Car("ABC124","Opel","Astra", CarTypeEnum.SEDAN,5);
        carRepository.save(car2);
        Car car3 = new Car("ABC125","Opel","Astra", CarTypeEnum.STATION_WAGON,5);
        carRepository.save(car3);
    }

    @AfterEach
    void cleanUp(){
        bookingRepository.deleteAll();
        bookingSequenceRepository.deleteAll();
        pessimisticLockRepository.deleteAll();
        carRepository.deleteAll();
    }

    @Test
    @Order(1)
    public void testConcurrentBooking() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        try {
            executorService.submit(() -> bookingService.createBooking(userId1, licencePlate, startDate, endDate));
            executorService.submit(() -> bookingService.createBooking(userId2, licencePlate, startDate.plusDays(3), endDate.plusDays(3)));
        } finally {
            executorService.shutdown();
        }
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        List<Booking> bookings = bookingRepository.findByCarLicencePlate(licencePlate);

        assertThat(bookings.size()).isEqualTo(2);
        assertThat(bookings).anySatisfy(booking -> assertThat(booking.getUserId()).isEqualTo(userId1));
        assertThat(bookings).anySatisfy(booking -> assertThat(booking.getUserId()).isEqualTo(userId2));
    }

    @Test
    @Order(2)
    public void testConcurrentBookingSameDates() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        try {
            executorService.submit(() -> bookingService.createBooking(userId1, licencePlate, startDate, endDate));
            executorService.submit(() -> bookingService.createBooking(userId2, licencePlate, startDate, endDate));
        } catch (Exception e){
        } finally {
            executorService.shutdown();
        }
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        assertThat(bookingRepository.findByCarLicencePlate(licencePlate).size()).isEqualTo(1);
    }

    @Test
    @Order(3)
    public void testConcurrentLocking() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        try {
            executorService.submit(() -> lockService.acquireLock(licencePlate, userId1));
            executorService.submit(() -> lockService.acquireLock(licencePlate, userId2));
        } finally {
            executorService.shutdown();
        }
        executorService.awaitTermination(5, TimeUnit.SECONDS);
        assertThat(pessimisticLockRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    @Order(4)
    public void testConcurrentLockingDifferentCars() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        try {
            executorService.submit(() -> lockService.acquireLock(licencePlate, userId1));
            executorService.submit(() -> lockService.acquireLock(licencePlate+"diff", userId2));
        } finally {
            executorService.shutdown();
        }
        executorService.awaitTermination(5, TimeUnit.SECONDS);
        assertThat(pessimisticLockRepository.findAll().size()).isEqualTo(2);
    }
}