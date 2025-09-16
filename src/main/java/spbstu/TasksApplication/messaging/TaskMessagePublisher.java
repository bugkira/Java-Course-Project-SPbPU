package spbstu.TasksApplication.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import spbstu.TasksApplication.config.RabbitMQConfig;
import spbstu.TasksApplication.model.Task;

@Component
@RequiredArgsConstructor
public class TaskMessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishTaskCreated(Task task) {
        TaskCreatedMessage message = TaskCreatedMessage.builder()
                .taskId(task.getTaskId())
                .title(task.getTitle())
                .description(task.getDescription())
                .userId(task.getUserId())
                .creationDate(task.getCreationDate())
                .targetDate(task.getTargetDate())
                .build();

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TASK_CREATED_EXCHANGE,
                RabbitMQConfig.TASK_CREATED_ROUTING_KEY,
                message
        );
    }
}
