package com.ferenc.reservation.amqp;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
public class AmqpConfiguration {

    @Value("${booking.routing.key}")
    private String bookingRoutingKey;

    @Value("${booking.exchange.name}")
    private String bookingExchangeName;

    @Value("${booking.queue.name}")
    private String bookingQueueName;

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
}
