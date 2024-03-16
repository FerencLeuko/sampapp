package com.ferenc.messaging.integration.handler;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import com.ferenc.commons.event.BookingEvent;
import com.ferenc.commons.event.EmailDeliveryEvent;

@Component
public class ResponseCreatingHandler {

    private final Logger logger = LoggerFactory.getLogger(ResponseCreatingHandler.class);

    @ServiceActivator
    public Message<EmailDeliveryEvent> handle(Message<BookingEvent> message) {
        BookingEvent bookingEvent = message.getPayload();
        EmailDeliveryEvent emailDeliveryEvent =
                EmailDeliveryEvent.builder()
                        .bookingId(bookingEvent.getBookingId())
                        .emailSent(LocalDateTime.now())
                        .build();
        Message<EmailDeliveryEvent> responseEventMessage = new GenericMessage<>(emailDeliveryEvent);
        logger.info("EmailDeliveryEvent message has been created, bookingId: {} , email sent: {}",
                emailDeliveryEvent.getBookingId(), emailDeliveryEvent.getEmailSent());
        return responseEventMessage;
    }
}
