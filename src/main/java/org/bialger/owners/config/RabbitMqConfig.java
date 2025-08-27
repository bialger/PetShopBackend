package org.bialger.owners.config;

import lombok.Getter;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class RabbitMqConfig {

    @Value("${app.messaging.exchange-name}")
    private String exchangeName;

    @Value("${app.messaging.request-queue-name}")
    private String requestQueueName;

    @Value("${app.messaging.request-routing-key}")
    private String requestRoutingKey;

    @Value("${app.messaging.reply-routing-key}")
    private String replyRoutingKey;

    @Bean
    public DirectExchange petshopExchange() {
        return new DirectExchange(exchangeName, true, false);
    }

    @Bean
    public Queue ownersRequestsQueue() {
        return new Queue(requestQueueName, true);
    }

    @Bean
    public Binding bindingOwnersRequests(DirectExchange exchange, Queue ownersRequestsQueue) {
        return BindingBuilder.bind(ownersRequestsQueue).to(exchange).with(requestRoutingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
}
