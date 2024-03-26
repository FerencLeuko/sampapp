package com.ferenc.messaging.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import com.ferenc.commons.event.BookingEvent;
import com.ferenc.messaging.integration.handler.EmailSendingHandler;
import com.ferenc.messaging.integration.handler.ResponseCreatingHandler;

@SpringBootTest
@EnableIntegration
@IntegrationComponentScan("com.ferenc.messaging")
class BookingEventFlowTest extends AbstractTest {

    public static final String INBOUND_CHANNEL = "inboundChannel";
    public static final int TIME_OUT = 1000;
    private InOrder inOrder;

    @MockBean
    private ResponseCreatingHandler responseCreatingHandler;

    @MockBean
    private EmailSendingHandler emailSendingHandler;

    @Autowired
    private BookingEventFlowTest.MockGateway mockGateway;

    @BeforeEach
    void setUp() {
        inOrder = inOrder(emailSendingHandler, responseCreatingHandler);
    }

    @AfterEach
    void verifyMocks() {
        verifyNoMoreInteractions(emailSendingHandler, responseCreatingHandler);
    }

    @Test
    void bookingIntegrationFlow() {
        BookingEvent bookingEvent = PODAM_FACTORY.manufacturePojo(BookingEvent.class);
        Message<BookingEvent> expected = new GenericMessage<>(bookingEvent);
        when(emailSendingHandler.handle(any())).thenReturn(expected);

        mockGateway.send(expected);

        ArgumentCaptor<Message<BookingEvent>> emailMessageCaptor = ArgumentCaptor.forClass(Message.class);
        inOrder.verify(emailSendingHandler, timeout(TIME_OUT)).handle(emailMessageCaptor.capture());

        Message<BookingEvent> capturedMessageForEmail = emailMessageCaptor.getValue();
        assertThat(expected.getPayload()).isEqualTo(capturedMessageForEmail.getPayload());

        ArgumentCaptor<Message<BookingEvent>> responseMessageCaptor = ArgumentCaptor.forClass(Message.class);
        inOrder.verify(responseCreatingHandler, timeout(TIME_OUT)).handle(responseMessageCaptor.capture());

        Message<BookingEvent> capturedMessageForResponse = responseMessageCaptor.getValue();
        assertThat(expected.getPayload()).isEqualTo(capturedMessageForResponse.getPayload());
    }

    @MessagingGateway
    interface MockGateway {

        @Gateway(requestChannel = INBOUND_CHANNEL)
        void send(Message payload);
    }
}