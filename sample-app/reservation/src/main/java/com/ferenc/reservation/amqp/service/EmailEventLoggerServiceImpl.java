package com.ferenc.reservation.amqp.service;

import com.ferenc.commons.event.*;
import com.ferenc.reservation.amqp.service.*;
import com.ferenc.reservation.repository.*;
import com.ferenc.reservation.repository.model.*;
import lombok.*;
import org.slf4j.*;
import org.springframework.stereotype.*;

@Service
@RequiredArgsConstructor
public class EmailEventLoggerServiceImpl implements EmailEventLoggerService
{
	
	private final Logger logger = LoggerFactory.getLogger( EmailEventLoggerService.class);
	private final EmailRepository emailRepository;
	
	@Override
	public EmailEventLog logEmailSent( EmailDeliveryEvent emailDeliveryEvent )
	{
		EmailEventLog emailEventLog = new EmailEventLog();
		emailEventLog.setBookingId(emailDeliveryEvent.getBookingId());
		emailEventLog.setEmailSent(emailDeliveryEvent.getEmailSent());
		emailRepository.save(emailEventLog);
		logger.info("EmailEventLog was saved, bookingId: {}", emailEventLog.getBookingId());
		return emailEventLog;
	}
}
