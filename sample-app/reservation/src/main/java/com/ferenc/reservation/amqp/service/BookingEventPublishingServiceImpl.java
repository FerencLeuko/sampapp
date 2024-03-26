package com.ferenc.reservation.amqp.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.ferenc.commons.event.BookingEvent;
import com.ferenc.reservation.amqp.AmqpConfiguration;
import com.ferenc.reservation.repository.model.Booking;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingEventPublishingServiceImpl implements BookingEventPublishingService {

    private final Logger logger = LoggerFactory.getLogger(BookingEventPublishingService.class);

    private final RabbitTemplate rabbitTemplate;
    private final AmqpConfiguration amqpConfiguration;

    @Override
    public void publishNewBookingEvent(Booking booking) {
        try {
            BookingEvent bookingEvent =
                    BookingEvent.builder()
                            .bookingId(booking.getBookingId())
                            .timeCreated(LocalDateTime.now())
                            .userId(booking.getUserId())
                            .lisencePlate(booking.getCar().getLicencePlate())
                            .startDate(booking.getStartDate())
                            .endDate(booking.getEndDate())
                            .build();

            rabbitTemplate
                    .convertAndSend(amqpConfiguration.getBookingExchangeName(),
                            amqpConfiguration.getBookingRoutingKey(), bookingEvent);

            logger.info("BookingEvent has been published, bookingId: {}", booking.getBookingId());

        } catch (Throwable e) {
            logger.error("BookingEvent has not been published, bookingId: {}. {}", booking.getBookingId(), e);
        }
    }

}
