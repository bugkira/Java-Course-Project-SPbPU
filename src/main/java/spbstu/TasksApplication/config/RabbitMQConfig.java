package spbstu.TasksApplication.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String TASK_CREATED_EXCHANGE = "task.created.exchange";
    public static final String TASK_CREATED_QUEUE = "task.created.queue";
    public static final String TASK_CREATED_ROUTING_KEY = "task.created";

    @Bean
    public Queue taskCreatedQueue() {
        return new Queue(TASK_CREATED_QUEUE);
    }

    @Bean
    public DirectExchange taskCreatedExchange() {
        return new DirectExchange(TASK_CREATED_EXCHANGE);
    }

    @Bean
    public Binding taskCreatedBinding() {
        return BindingBuilder
                .bind(taskCreatedQueue())
                .to(taskCreatedExchange())
                .with(TASK_CREATED_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
