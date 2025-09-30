package org.example.taskmanager.service;

import java.time.LocalDateTime;
import java.util.List;

import org.example.taskmanager.domain.NotificationEntity;
import org.example.taskmanager.domain.TaskEntity;
import org.example.taskmanager.domain.UserEntity;
import org.example.taskmanager.repository.NotificationDataRepository;
import org.example.taskmanager.repository.UserDataRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncTaskService {

    private final UserDataRepository userRepository;
    private final NotificationDataRepository notificationRepository;

    @Async("taskExecutor")
    public void processOverdueTasks(List<TaskEntity> overdueTasks) {
        log.info("Processing {} overdue tasks asynchronously", overdueTasks.size());
        
        for (TaskEntity task : overdueTasks) {
            try {
                processOverdueTask(task);
            } catch (Exception e) {
                log.error("Error processing overdue task {}: {}", task.getId(), e.getMessage(), e);
            }
        }
        
        log.info("Completed processing overdue tasks");
    }

    @Async("taskExecutor")
    public void createOverdueNotification(TaskEntity task, UserEntity user) {
        try {
            log.info("Creating overdue notification for task {} and user {}", task.getId(), user.getLogin());
            
            NotificationEntity notification = NotificationEntity.builder()
                    .message("Your task '" + task.getName() + "' was due on " + task.getDueDate() + " and is now overdue.")
                    .relatedTaskId(task.getId())
                    .recipientId(user.getId())
                    .timestamp(LocalDateTime.now())
                    .viewed(false)
                    .build();
            
            notificationRepository.save(notification);
            
            log.info("Successfully created overdue notification for task {} and user {}", task.getId(), user.getLogin());
        } catch (Exception e) {
            log.error("Error creating overdue notification for task {} and user {}: {}", 
                    task.getId(), user.getLogin(), e.getMessage(), e);
        }
    }

    private void processOverdueTask(TaskEntity task) {
        log.debug("Processing overdue task: {}", task.getName());
        
        // Get user information
        UserEntity user = userRepository.findById(task.getOwnerId())
                .orElse(null);
        
        if (user == null) {
            log.warn("User not found for overdue task {}: {}", task.getId(), task.getOwnerId());
            return;
        }
        
        // Create notification asynchronously
        createOverdueNotification(task, user);
    }
}
