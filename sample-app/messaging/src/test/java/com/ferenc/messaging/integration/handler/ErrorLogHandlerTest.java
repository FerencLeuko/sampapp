package com.ferenc.messaging.integration.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.ErrorMessage;

import com.ferenc.messaging.integration.AbstractTest;

@ExtendWith({ MockitoExtension.class })
public class ErrorLogHandlerTest extends AbstractTest {

    @InjectMocks
    private ErrorLogHandler errorLogHandler;

    @Test
    void testHandle() {
        String message = PODAM_FACTORY.manufacturePojo(String.class);
        Exception cause = new RuntimeException(message);
        Message<Throwable> errorMessage = new ErrorMessage(cause);
        Exception exception = new MessagingException(errorMessage, cause);

        ErrorMessage expected = new ErrorMessage(exception, errorMessage);
        ErrorMessage actual = errorLogHandler.handle(expected);
        assertEquals(expected.getClass(), actual.getClass());
        assertEquals(expected, actual);
    }

}
