package com.ferenc.messaging.handler;

import com.ferenc.commons.event.BookingEvent;
import com.ferenc.messaging.integration.handler.EmailSendingHandler;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.endsWith;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@SpringBootTest
public class EmailSendingHandlerTest {

    @Mock
    private JavaMailSender emailSender;
    
    @AfterEach
	private void verifyMocks(){
    	verifyNoMoreInteractions(emailSender);
	}

    @Test
    void testHandle(){
        EmailSendingHandler emailSendingHandler = new EmailSendingHandler(emailSender);
        PodamFactory factory = new PodamFactoryImpl();
        BookingEvent bookingEvent = factory.manufacturePojo(BookingEvent.class);
        Message<BookingEvent> expected = new GenericMessage<>(bookingEvent);
        Message<BookingEvent> actual = emailSendingHandler.handle(expected);
        assertEquals(expected.getPayload(),actual.getPayload());
        verify(emailSender).send(any(SimpleMailMessage.class));
    }
}
