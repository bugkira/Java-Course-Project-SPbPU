package spbstu.TasksApplication.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import spbstu.TasksApplication.config.RabbitMQConfig;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskMessageListener {

    @RabbitListener(queues = RabbitMQConfig.TASK_CREATED_QUEUE)
    public void handleTaskCreated(TaskCreatedMessage message) {
        log.info("Received task created message: {}", message);
        // Process task creation event
    }
}
