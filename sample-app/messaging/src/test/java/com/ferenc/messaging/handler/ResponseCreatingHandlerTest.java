package com.ferenc.messaging.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import com.ferenc.commons.event.BookingEvent;
import com.ferenc.commons.event.EmailDeliveryEvent;
import com.ferenc.messaging.integration.handler.ResponseCreatingHandler;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@SpringBootTest
class ResponseCreatingHandlerTest {

    private static final PodamFactory PODAM_FACTORY = new PodamFactoryImpl();

    @Autowired
    private ResponseCreatingHandler responseCreatingHandler;

    @Test
    void testHandle() {
        BookingEvent bookingEvent = PODAM_FACTORY.manufacturePojo(BookingEvent.class);
        Message<BookingEvent> bookingEventMessage = new GenericMessage<>(bookingEvent);
        Message<EmailDeliveryEvent> actual = responseCreatingHandler.handle(bookingEventMessage);

        EmailDeliveryEvent emailDeliveryEventExpected =
                EmailDeliveryEvent.builder()
                        .bookingId(bookingEvent.getBookingId())
                        .emailSent(actual.getPayload().getEmailSent())
                        .build();
        Message<EmailDeliveryEvent> expected = new GenericMessage<>(emailDeliveryEventExpected);

        assertEquals(expected.getClass(), actual.getClass());
        assertEquals(expected.getPayload(), actual.getPayload());
    }
}
