package com.ferenc.messaging.amqp;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.amqp.dsl.AmqpInboundChannelAdapterSMLCSpec;
import org.springframework.integration.amqp.dsl.AmqpOutboundChannelAdapterSpec;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Configuration
@Getter
@RequiredArgsConstructor
public class AmqpConfiguration {

    private final AmqpTemplate amqpTemplate;

    private final ConnectionFactory connectionFactory;

    @Value("${booking.routing.key}")
    public String bookingRoutingKey;

    @Value("${booking.queue.name}")
    private String bookingQueueName;

    @Value("${booking.exchange.name}")
    private String bookingExchangeName;

    @Value("${response.exchange.name}")
    private String responseExchangeName;

    @Value("${response.queue.name}")
    private String responseQueueName;

    @Value("${response.routing.key}")
    private String responseRoutingKey;

    @Value("${error.exchange.name}")
    private String errorExchangeName;

    @Value("${error.queue.name}")
    private String errorQueueName;

    @Value("${error.routing.key}")
    private String errorRoutingKey;

    @Bean
    Queue bookingQueue() {
        return new Queue(bookingQueueName, false);
    }

    @Bean
    DirectExchange bookingExchange() {
        return new DirectExchange(bookingExchangeName);
    }

    @Bean
    Binding bookingBinding() {
        return BindingBuilder
                .bind(bookingQueue())
                .to(bookingExchange())
                .with(bookingRoutingKey);
    }

    @Bean
    Exchange responseExchange() {
        return ExchangeBuilder
                .directExchange(responseExchangeName)
                .build();
    }

    @Bean
    Queue responseQueue() {
        return QueueBuilder
                .nonDurable(responseQueueName)
                .build();
    }

    @Bean
    Binding responseBinding() {
        return BindingBuilder
                .bind(responseQueue())
                .to(responseExchange())
                .with(responseRoutingKey)
                .noargs();
    }

    @Bean
    Exchange errorExchange() {
        return ExchangeBuilder
                .directExchange(errorExchangeName)
                .build();
    }

    @Bean
    Queue errorQueue() {
        return QueueBuilder
                .nonDurable(errorQueueName)
                .build();
    }

    @Bean
    Binding errorBinding() {
        return BindingBuilder
                .bind(errorQueue())
                .to(errorExchange())
                .with(errorRoutingKey)
                .noargs();
    }

    @Bean
    public AmqpInboundChannelAdapterSMLCSpec inboundAdapter() {
        return Amqp.inboundAdapter(connectionFactory, getBookingQueueName());
    }

    @Bean
    public AmqpOutboundChannelAdapterSpec outboundAdapter() {
        return Amqp.outboundAdapter(amqpTemplate).exchangeName(getResponseExchangeName()).routingKey(getResponseRoutingKey());
    }

    @Bean
    public AmqpOutboundChannelAdapterSpec outboundAdapterError() {
        return Amqp.outboundAdapter(amqpTemplate).exchangeName(getErrorExchangeName()).routingKey(getErrorRoutingKey());
    }

}
