package com.ferenc.reservation.amqp.service;

import com.ferenc.commons.event.EmailDeliveryEvent;
import com.ferenc.reservation.repository.model.EmailEventLog;

public interface EmailEventLoggerService {

    EmailEventLog logEmailSent(EmailDeliveryEvent emailDeliveryEvent);
}
