package com.ferenc.reservation.amqp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ferenc.commons.event.EmailDeliveryEvent;
import com.ferenc.reservation.repository.EmailRepository;
import com.ferenc.reservation.repository.model.EmailEventLog;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailEventLoggerServiceImpl implements EmailEventLoggerService {

    private final Logger logger = LoggerFactory.getLogger(EmailEventLoggerService.class);
    private final EmailRepository emailRepository;

    @Override
    public EmailEventLog logEmailSent(EmailDeliveryEvent emailDeliveryEvent) {
        EmailEventLog emailEventLog = new EmailEventLog();
        emailEventLog.setBookingId(emailDeliveryEvent.getBookingId());
        emailEventLog.setEmailSent(emailDeliveryEvent.getEmailSent());
        emailRepository.save(emailEventLog);
        logger.info("EmailEventLog was saved, bookingId: {}", emailEventLog.getBookingId());
        return emailEventLog;
    }
}
