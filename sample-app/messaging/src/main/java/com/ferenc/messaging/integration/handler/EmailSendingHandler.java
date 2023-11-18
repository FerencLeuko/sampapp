package com.ferenc.messaging.integration.handler;

import com.ferenc.commons.event.BookingEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.Message;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class EmailSendingHandler {

    private final Logger logger = LoggerFactory.getLogger(EmailSendingHandler.class);

    private final JavaMailSender emailSender;

    @Value("${email.from}")
    private String from;
    @Value("${email.subject}")
    private String subject;

    @ServiceActivator
    @Retryable
    public Message<BookingEvent> handle(Message<BookingEvent> message) {
        BookingEvent event = message.getPayload();
        String content = event.toString();
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(from);
        simpleMailMessage.setTo(event.getUserId());
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(content);
        emailSender.send(simpleMailMessage);
        logger.info("Email has been created, bookingId: {}" , event.getBookingId());
        return message;
    }
}
