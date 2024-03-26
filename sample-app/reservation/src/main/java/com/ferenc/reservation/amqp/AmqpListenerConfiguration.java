package com.ferenc.reservation.amqp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.Payload;

import com.ferenc.commons.event.EmailDeliveryEvent;
import com.ferenc.reservation.amqp.service.EmailEventLoggerService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class AmqpListenerConfiguration {

    private final Logger logger = LoggerFactory.getLogger(AmqpListenerConfiguration.class);
    private final EmailEventLoggerService emailEventService;

    @RabbitListener(queues = "${response.queue.name}")
    public void getResponseEventFromMessaging(@Payload EmailDeliveryEvent payload) {
        emailEventService.logEmailSent(payload);
    }

    @RabbitListener(queues = "${error.queue.name}")
    public void getErrorResponseFromMessaging(@Payload Exception exception) {
        logger.error(String.format("Error, email was not sent: %s", exception.getCause()));
    }
}
