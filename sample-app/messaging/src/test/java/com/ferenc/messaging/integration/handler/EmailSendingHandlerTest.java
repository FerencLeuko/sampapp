package com.ferenc.messaging.integration.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import com.ferenc.commons.event.BookingEvent;
import com.ferenc.messaging.integration.AbstractTest;

@ExtendWith({ MockitoExtension.class })
class EmailSendingHandlerTest extends AbstractTest {

    @Mock
    private JavaMailSender emailSender;

    @InjectMocks
    EmailSendingHandler emailSendingHandler;

    @AfterEach
    public void verifyMocks() {
        verifyNoMoreInteractions(emailSender);
    }

    @Test
    void testHandle() {
        BookingEvent bookingEvent = PODAM_FACTORY.manufacturePojo(BookingEvent.class);
        Message<BookingEvent> expected = new GenericMessage<>(bookingEvent);
        Message<BookingEvent> actual = emailSendingHandler.handle(expected);
        assertEquals(expected.getPayload(), actual.getPayload());
        verify(emailSender).send(any(SimpleMailMessage.class));
    }
}
