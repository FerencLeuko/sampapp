package com.ferenc.reservation.amqp.service;

import com.ferenc.commons.event.BookingEvent;
import com.ferenc.reservation.amqp.*;
import com.ferenc.reservation.repository.model.Booking;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookingEventPublishingServiceImpl implements BookingEventPublishingService
{

    private final RabbitTemplate rabbitTemplate;
    private final AmqpConfiguration amqpConfiguration;
    
    @Override
    public void publishNewBookingEvent(Booking booking) {
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
                .convertAndSend( amqpConfiguration.getBookingExchangeName(),
                        amqpConfiguration.getBookingRoutingKey(), bookingEvent);
    }

}
