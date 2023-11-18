package com.ferenc.reservation.amqp;

import com.ferenc.commons.event.*;
import com.ferenc.reservation.amqp.service.*;
import lombok.*;
import org.slf4j.*;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.messaging.handler.annotation.*;

@Configuration
@RequiredArgsConstructor
public class AmqpListenerConfiguration
{

    private final Logger logger = LoggerFactory.getLogger( AmqpListenerConfiguration.class);
    private final EmailEventLoggerService emailEventService;

    @RabbitListener(queues = "${response.queue.name}")
    public void getResponseEventFromMessaging(@Payload EmailDeliveryEvent payload) {
        emailEventService.logEmailSent( payload );
    }
    
    @RabbitListener(queues = "${error.queue.name}")
    public void getErrorResponseFromMessaging(@Payload Exception exception) {
        logger.error("Error, email was not sent: " + exception.getCause());
    }
}
