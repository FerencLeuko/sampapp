package com.ferenc.reservation.businessservice;

import com.ferenc.reservation.amqp.service.BookingEventPublishingService;
import com.ferenc.reservation.exception.CarNotAvailableException;
import com.ferenc.reservation.exception.NoSuchBookingException;
import com.ferenc.reservation.repository.BookingRepository;
import com.ferenc.reservation.repository.BookingSequenceHelper;
import com.ferenc.reservation.repository.CarRepository;
import com.ferenc.reservation.repository.model.Booking;
import com.ferenc.reservation.repository.model.Car;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BookingBusinessServiceImpl implements BookingBusinessService {

    private final Logger logger = LoggerFactory.getLogger(BookingBusinessService.class);

    private final CarRepository carRepository;
    private final BookingRepository bookingRepository;
    private final BookingSequenceHelper bookingSequenceHelper;
    private final BookingEventPublishingService bookingEventPublishingService;

    @Override
    public Booking createBooking(String userId, String licencePlate, LocalDate startDate, LocalDate endDate) {
        Car car = carRepository.findByLicencePlate(licencePlate)
                .orElseThrow(() -> new CarNotAvailableException("This car is not available in our system: " + licencePlate +"."));
        Booking booking = new Booking();
        booking.setCar(car);
        booking.setUserId(userId);
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        
        synchronized (this) {
            if (!isCarAvailable(licencePlate, startDate, endDate)) {
                throw new CarNotAvailableException("This car: " + licencePlate + " is not available for this time range: " + startDate + " - " + endDate +".");
            }
            booking.setBookingId(bookingSequenceHelper.getNextSequence());
            bookingRepository.save(booking);
        }
        
        logger.info("Booking has been created, bookingId: {}", booking.getBookingId());
        bookingEventPublishingService.publishNewBookingEvent(booking);
        logger.info("BookingEvent has been published, bookingId: {}", booking.getBookingId());
        return booking;
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public List<Booking> getAllBookingsByUserId(String userId) {
        return bookingRepository.findByUserId(userId);
    }

    @Override
    public Booking getBooking(Integer bookingId) {
        return bookingRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new NoSuchBookingException("This booking does not exists: " + bookingId + "."));
    }

    @Override
    public Booking updateBooking(Integer bookingId, LocalDate startDate, LocalDate endDate) {
        Booking booking = getBooking(bookingId);
        String licencePlate = booking.getCar().getLicencePlate();

        synchronized (this) {
            if (!isCarAvailable(licencePlate, startDate, endDate, Optional.of(booking))) {
                throw new CarNotAvailableException("This car: " + licencePlate + " is not available for this time range: " + startDate + " - " + endDate +".");
            }
            booking.setStartDate(startDate);
            booking.setEndDate(endDate);
            bookingRepository.save(booking);
        }
        
        logger.info("Booking has been updated, bookingId: {}", booking.getBookingId());
        return booking;
    }

    @Override
    public Booking deleteBooking(Integer bookingId) {
        Booking bookingToDelete = getBooking(bookingId);
        bookingRepository.deleteById(bookingId);
        logger.info("Booking has been deleted, bookingId: {}", bookingToDelete.getBookingId());
        return bookingToDelete;
    }

    @Override
    public boolean isCarAvailable(String licencePlate, LocalDate startDate, LocalDate endDate){
        return isCarAvailable(licencePlate, startDate, endDate, Optional.empty());
    }
    
	private boolean isCarAvailable(String licencePlate, LocalDate startDate, LocalDate endDate, Optional<Booking> skip){
		List<Booking> bookingsForTheCar = bookingRepository.findByCarLicencePlate(licencePlate);
		if(skip.isPresent()){
			bookingsForTheCar.remove(skip);
		}
		for(Booking booking: bookingsForTheCar){
			if(!booking.getStartDate().isBefore(startDate) && !booking.getStartDate().isAfter(endDate)
					|| !booking.getEndDate().isBefore(startDate) && !booking.getEndDate().isAfter(endDate)
			){
				return false;
			}
		}
		return true;
	}
}
