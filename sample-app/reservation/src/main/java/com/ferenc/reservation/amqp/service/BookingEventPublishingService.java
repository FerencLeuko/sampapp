package com.ferenc.reservation.amqp.service;

import com.ferenc.reservation.repository.model.Booking;

public interface BookingEventPublishingService {

    void publishNewBookingEvent(Booking booking);
}
