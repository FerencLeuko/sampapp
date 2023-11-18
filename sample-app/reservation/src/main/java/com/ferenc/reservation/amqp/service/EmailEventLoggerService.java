package com.ferenc.reservation.amqp.service;

import com.ferenc.commons.event.*;
import com.ferenc.reservation.repository.model.*;

public interface EmailEventLoggerService
{
	EmailEventLog logEmailSent( EmailDeliveryEvent emailDeliveryEvent);
}
