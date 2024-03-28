package com.ferenc.messaging.integration.handler;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import com.ferenc.commons.event.BookingEvent;
import com.ferenc.commons.event.EmailDeliveryEvent;
import com.ferenc.messaging.integration.AbstractTest;

@ExtendWith({ MockitoExtension.class })
class ResponseCreatingHandlerTest extends AbstractTest {

    @InjectMocks
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

        assertThat(actual.getPayload()).isEqualTo(expected.getPayload());
    }
}
