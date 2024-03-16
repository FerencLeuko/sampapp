package com.ferenc.messaging.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import com.ferenc.commons.event.BookingEvent;
import com.ferenc.messaging.integration.handler.EmailSendingHandler;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@SpringBootTest
class EmailSendingHandlerTest {

    @Mock
    private JavaMailSender emailSender;

    @AfterEach
    public void verifyMocks() {
        verifyNoMoreInteractions(emailSender);
    }

    @Test
    void testHandle() {
        EmailSendingHandler emailSendingHandler = new EmailSendingHandler(emailSender);
        PodamFactory factory = new PodamFactoryImpl();
        BookingEvent bookingEvent = factory.manufacturePojo(BookingEvent.class);
        Message<BookingEvent> expected = new GenericMessage<>(bookingEvent);
        Message<BookingEvent> actual = emailSendingHandler.handle(expected);
        assertEquals(expected.getPayload(), actual.getPayload());
        verify(emailSender).send(any(SimpleMailMessage.class));
    }
}
