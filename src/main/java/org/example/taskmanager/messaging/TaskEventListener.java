package org.example.taskmanager.messaging;

import java.time.LocalDateTime;

import org.example.taskmanager.config.RabbitMQConfig;
import org.example.taskmanager.domain.NotificationEntity;
import org.example.taskmanager.repository.NotificationDataRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@ConditionalOnProperty(name = "spring.rabbitmq.host")
@RequiredArgsConstructor
@Slf4j
public class TaskEventListener {

    private final NotificationDataRepository notificationRepository;

    @RabbitListener(queues = RabbitMQConfig.TASK_CREATED_QUEUE)
    @CacheEvict(value = "notifications", allEntries = true)
    public void handleTaskCreated(TaskEventMessage message) {
        try {
            log.info("Received task created event for task ID: {}, owner: {}", 
                    message.getTaskId(), message.getOwnerLogin());

            // Create notification for task creation
            NotificationEntity notification = NotificationEntity.builder()
                    .message("New task created: " + message.getTaskName())
                    .relatedTaskId(message.getTaskId())
                    .recipientId(message.getOwnerId())
                    .viewed(false)
                    .timestamp(LocalDateTime.now())
                    .build();

            notificationRepository.save(notification);
            
            log.info("Created notification for task creation: task ID {}, recipient ID {}", 
                    message.getTaskId(), message.getOwnerId());
        } catch (Exception e) {
            log.error("Failed to process task created event for task ID: {}", message.getTaskId(), e);
            // Re-throw to trigger retry mechanism
            throw e;
        }
    }

    @RabbitListener(queues = RabbitMQConfig.TASK_UPDATED_QUEUE)
    @CacheEvict(value = "notifications", allEntries = true)
    public void handleTaskUpdated(TaskEventMessage message) {
        try {
            log.info("Received task updated event for task ID: {}, owner: {}", 
                    message.getTaskId(), message.getOwnerLogin());

            // Create notification for task update
            NotificationEntity notification = NotificationEntity.builder()
                    .message("Task updated: " + message.getTaskName())
                    .relatedTaskId(message.getTaskId())
                    .recipientId(message.getOwnerId())
                    .viewed(false)
                    .timestamp(LocalDateTime.now())
                    .build();

            notificationRepository.save(notification);
            
            log.info("Created notification for task update: task ID {}, recipient ID {}", 
                    message.getTaskId(), message.getOwnerId());
        } catch (Exception e) {
            log.error("Failed to process task updated event for task ID: {}", message.getTaskId(), e);
            // Re-throw to trigger retry mechanism
            throw e;
        }
    }

    @RabbitListener(queues = RabbitMQConfig.TASK_DELETED_QUEUE)
    @CacheEvict(value = "notifications", allEntries = true)
    public void handleTaskDeleted(TaskEventMessage message) {
        try {
            log.info("Received task deleted event for task ID: {}, owner: {}", 
                    message.getTaskId(), message.getOwnerLogin());

            // Create notification for task deletion
            NotificationEntity notification = NotificationEntity.builder()
                    .message("Task deleted: " + message.getTaskName())
                    .relatedTaskId(message.getTaskId())
                    .recipientId(message.getOwnerId())
                    .viewed(false)
                    .timestamp(LocalDateTime.now())
                    .build();

            notificationRepository.save(notification);
            
            log.info("Created notification for task deletion: task ID {}, recipient ID {}", 
                    message.getTaskId(), message.getOwnerId());
        } catch (Exception e) {
            log.error("Failed to process task deleted event for task ID: {}", message.getTaskId(), e);
            // Re-throw to trigger retry mechanism
            throw e;
        }
    }
}
