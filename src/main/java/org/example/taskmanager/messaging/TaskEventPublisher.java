package org.example.taskmanager.messaging;

import org.example.taskmanager.config.RabbitMQConfig;
import org.example.taskmanager.domain.TaskEntity;
import org.example.taskmanager.domain.UserEntity;
import org.example.taskmanager.repository.UserDataRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@ConditionalOnProperty(name = "spring.rabbitmq.host")
@RequiredArgsConstructor
@Slf4j
public class TaskEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final UserDataRepository userRepository;

    public void publishTaskCreated(TaskEntity task) {
        try {
            UserEntity owner = userRepository.findById(task.getOwnerId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + task.getOwnerId()));
            
            TaskEventMessage message = TaskEventMessage.fromTaskCreated(task, owner.getLogin(), owner.getEmailAddress());
            
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.TASK_EXCHANGE,
                    RabbitMQConfig.TASK_CREATED_ROUTING_KEY,
                    message
            );
            
            log.info("Published task created event for task ID: {}, owner: {}", task.getId(), owner.getLogin());
        } catch (Exception e) {
            log.error("Failed to publish task created event for task ID: {}", task.getId(), e);
            // Don't throw exception to avoid breaking the main flow
        }
    }

    public void publishTaskUpdated(TaskEntity task) {
        try {
            UserEntity owner = userRepository.findById(task.getOwnerId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + task.getOwnerId()));
            
            TaskEventMessage message = TaskEventMessage.fromTaskUpdated(task, owner.getLogin(), owner.getEmailAddress());
            
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.TASK_EXCHANGE,
                    RabbitMQConfig.TASK_UPDATED_ROUTING_KEY,
                    message
            );
            
            log.info("Published task updated event for task ID: {}, owner: {}", task.getId(), owner.getLogin());
        } catch (Exception e) {
            log.error("Failed to publish task updated event for task ID: {}", task.getId(), e);
            // Don't throw exception to avoid breaking the main flow
        }
    }

    public void publishTaskDeleted(Long taskId, String taskName, Long ownerId) {
        try {
            UserEntity owner = userRepository.findById(ownerId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + ownerId));
            
            TaskEventMessage message = TaskEventMessage.fromTaskDeleted(taskId, taskName, ownerId, 
                    owner.getLogin(), owner.getEmailAddress());
            
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.TASK_EXCHANGE,
                    RabbitMQConfig.TASK_DELETED_ROUTING_KEY,
                    message
            );
            
            log.info("Published task deleted event for task ID: {}, owner: {}", taskId, owner.getLogin());
        } catch (Exception e) {
            log.error("Failed to publish task deleted event for task ID: {}", taskId, e);
            // Don't throw exception to avoid breaking the main flow
        }
    }
}
