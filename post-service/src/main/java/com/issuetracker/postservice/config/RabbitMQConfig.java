




package com.issuetracker.postservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue}")
    private String queueName;

    @Value("${rabbitmq.exchange}")
    private String exchangeName;

    @Value("${rabbitmq.routing-key}")
    private String routingKey;

    @Value("${rabbitmq.update-queue}")
    private String updateQueueName;

    @Value("${rabbitmq.update-routing-key}")
    private String updateRoutingKey;

    @Value("${rabbitmq.upvote-queue}")
    private String upvoteQueueName;

    @Value("${rabbitmq.upvote-routing-key}")
    private String upvoteRoutingKey;

    @Bean
    public Queue queue() {
        return new Queue(queueName, true); // durable=true
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(routingKey);
    }

    // Update Post Queue and Binding
    @Bean
    public Queue updateQueue() {
        return new Queue(updateQueueName, true);
    }

    @Bean
    public Binding updateBinding(Queue updateQueue, TopicExchange exchange) {
        return BindingBuilder
                .bind(updateQueue)
                .to(exchange)
                .with(updateRoutingKey);
    }

    // Upvote Queue and Binding
    @Bean
    public Queue upvoteQueue() {
        return new Queue(upvoteQueueName, true);
    }

    @Bean
    public Binding upvoteBinding(Queue upvoteQueue, TopicExchange exchange) {
        return BindingBuilder
                .bind(upvoteQueue)
                .to(exchange)
                .with(upvoteRoutingKey);
    }

    @Bean
    public MessageConverter messageConverter() {
        // Create ObjectMapper with Java 8 date/time support
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        // Create Jackson converter with configured ObjectMapper
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}

