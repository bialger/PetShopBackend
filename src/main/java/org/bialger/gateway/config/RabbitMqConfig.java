package org.bialger.gateway.config;

import lombok.Getter;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
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

    @Value("${app.messaging.queues.owners-request}")
    private String ownersRequestsQueue;

    @Value("${app.messaging.queues.pets-request}")
    private String petsRequestsQueue;

    @Value("${app.messaging.queues.gateway-replies}")
    private String gatewayRepliesQueue;

    @Value("${app.messaging.routing-keys.owners-request}")
    private String ownersRequestsRoutingKey;

    @Value("${app.messaging.routing-keys.pets-request}")
    private String petsRequestsRoutingKey;

    @Value("${app.messaging.routing-keys.gateway-replies}")
    private String gatewayRepliesRoutingKey;

    @Bean
    public DirectExchange petshopExchange() {
        return new DirectExchange(exchangeName, true, false);
    }

    @Bean
    public Queue ownersRequestsQueue() {
        return new Queue(ownersRequestsQueue, true);
    }

    @Bean
    public Queue petsRequestsQueue() {
        return new Queue(petsRequestsQueue, true);
    }

    @Bean
    public Queue gatewayRepliesQueue() {
        return new Queue(gatewayRepliesQueue, true);
    }

    @Bean
    public Binding bindingOwnersRequests(DirectExchange exchange, Queue ownersRequestsQueue) {
        return BindingBuilder.bind(ownersRequestsQueue).to(exchange).with(ownersRequestsRoutingKey);
    }

    @Bean
    public Binding bindingPetsRequests(DirectExchange exchange, Queue petsRequestsQueue) {
        return BindingBuilder.bind(petsRequestsQueue).to(exchange).with(petsRequestsRoutingKey);
    }

    @Bean
    public Binding bindingGatewayReplies(DirectExchange exchange, Queue gatewayRepliesQueue) {
        return BindingBuilder.bind(gatewayRepliesQueue).to(exchange).with(gatewayRepliesRoutingKey);
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
