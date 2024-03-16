package com.ferenc.messaging.integration.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.ErrorMessage;
import org.springframework.stereotype.Component;

@Component
public class ErrorLogHandler {

    private final Logger logger = LoggerFactory.getLogger(ErrorLogHandler.class);

    @ServiceActivator
    public ErrorMessage handle(ErrorMessage errorMessage) {
        MessagingException exception = (MessagingException) errorMessage.getPayload();
        logger.error("Error, failed message: {} , cause: {}", exception.getFailedMessage().getPayload(), String.valueOf(exception.getCause()));
        return errorMessage;
    }
}
