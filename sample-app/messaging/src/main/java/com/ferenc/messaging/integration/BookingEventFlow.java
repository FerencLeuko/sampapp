package com.ferenc.messaging.integration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.handler.LoggingHandler;

import com.ferenc.messaging.amqp.AmqpConfiguration;
import com.ferenc.messaging.integration.handler.EmailSendingHandler;
import com.ferenc.messaging.integration.handler.ErrorLogHandler;
import com.ferenc.messaging.integration.handler.ResponseCreatingHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class BookingEventFlow {

    public static final String ERROR_CHANNEL = "ERROR";
    private final AmqpConfiguration amqpConfiguration;
    private final ResponseCreatingHandler responseCreatingHandler;
    private final EmailSendingHandler emailSendingHandler;
    private final ErrorLogHandler errorLogHandler;

    @Bean
    public IntegrationFlow inboundIntegrationFlow() {
        return IntegrationFlow
                .from(amqpConfiguration.inboundAdapter())
                .enrichHeaders(headerEnricherSpec -> headerEnricherSpec.errorChannel(ERROR_CHANNEL))
                .channel(inboundChannel())
                .get();
    }

    @Bean
    public IntegrationFlow bookingIntegrationFlow() {
        return IntegrationFlow
                .from(inboundChannel())
                .enrichHeaders(headerEnricherSpec -> headerEnricherSpec.errorChannel(ERROR_CHANNEL))
                .log(LoggingHandler.Level.INFO, new LiteralExpression("BookingEvent recieved from RabbitMQ."))
                .handle(emailSendingHandler)
                .log(LoggingHandler.Level.INFO, new LiteralExpression("Email sent."))
                .handle(responseCreatingHandler)
                .log(LoggingHandler.Level.INFO, new LiteralExpression("EmailDeliveryEvent sent to RabbitMQ."))
                .channel(outboundChannel())
                .get();
    }

    @Bean
    public IntegrationFlow outboundIntegrationFlow() {
        return IntegrationFlow
                .from(outboundChannel())
                .enrichHeaders(headerEnricherSpec -> headerEnricherSpec.errorChannel(ERROR_CHANNEL))
                .handle(amqpConfiguration.outboundAdapter())
                .get();
    }

    @Bean
    public IntegrationFlow bookingIntegrationFlowError() {
        return IntegrationFlow.from(ERROR_CHANNEL)
                .handle(errorLogHandler)
                .log(LoggingHandler.Level.INFO, new LiteralExpression("Email was not sent."))
                .handle(amqpConfiguration.outboundAdapterError())
                .get();
    }

    @Bean
    public ExecutorChannel inboundChannel() {
        return new ExecutorChannel(new SimpleAsyncTaskExecutor());
    }

    @Bean
    public ExecutorChannel outboundChannel() {
        return new ExecutorChannel(new SimpleAsyncTaskExecutor());
    }

}
