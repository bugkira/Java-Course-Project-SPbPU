package org.example.taskmanager.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "spring.rabbitmq.host")
public class RabbitMQConfig {

    // Queue names
    public static final String TASK_CREATED_QUEUE = "task.created.queue";
    public static final String TASK_UPDATED_QUEUE = "task.updated.queue";
    public static final String TASK_DELETED_QUEUE = "task.deleted.queue";
    
    // Exchange names
    public static final String TASK_EXCHANGE = "task.exchange";
    
    // Routing keys
    public static final String TASK_CREATED_ROUTING_KEY = "task.created";
    public static final String TASK_UPDATED_ROUTING_KEY = "task.updated";
    public static final String TASK_DELETED_ROUTING_KEY = "task.deleted";

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        return factory;
    }

    // Exchange
    @Bean
    public TopicExchange taskExchange() {
        return new TopicExchange(TASK_EXCHANGE);
    }

    // Queues
    @Bean
    public Queue taskCreatedQueue() {
        return QueueBuilder.durable(TASK_CREATED_QUEUE).build();
    }

    @Bean
    public Queue taskUpdatedQueue() {
        return QueueBuilder.durable(TASK_UPDATED_QUEUE).build();
    }

    @Bean
    public Queue taskDeletedQueue() {
        return QueueBuilder.durable(TASK_DELETED_QUEUE).build();
    }

    // Bindings
    @Bean
    public Binding taskCreatedBinding() {
        return BindingBuilder
                .bind(taskCreatedQueue())
                .to(taskExchange())
                .with(TASK_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding taskUpdatedBinding() {
        return BindingBuilder
                .bind(taskUpdatedQueue())
                .to(taskExchange())
                .with(TASK_UPDATED_ROUTING_KEY);
    }

    @Bean
    public Binding taskDeletedBinding() {
        return BindingBuilder
                .bind(taskDeletedQueue())
                .to(taskExchange())
                .with(TASK_DELETED_ROUTING_KEY);
    }
}
