package org.example.taskmanager.service;

import java.time.LocalDateTime;
import java.util.List;

import org.example.taskmanager.domain.TaskEntity;
import org.example.taskmanager.repository.TaskDataRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskSchedulerService {

    private final TaskDataRepository taskRepository;
    private final AsyncTaskService asyncTaskService;

    /**
     * Check for overdue tasks every hour
     * Runs at the top of every hour (e.g., 1:00, 2:00, 3:00, etc.)
     */
    @Scheduled(cron = "0 0 * * * *")
    public void checkOverdueTasks() {
        log.info("Starting scheduled check for overdue tasks at {}", LocalDateTime.now());
        
        try {
            LocalDateTime currentTime = LocalDateTime.now();
            List<TaskEntity> overdueTasks = taskRepository.findOverdueTasks(currentTime);
            
            if (overdueTasks.isEmpty()) {
                log.info("No overdue tasks found");
                return;
            }
            
            log.info("Found {} overdue tasks", overdueTasks.size());
            
            // Process overdue tasks asynchronously
            asyncTaskService.processOverdueTasks(overdueTasks);
            
        } catch (Exception e) {
            log.error("Error during scheduled overdue task check: {}", e.getMessage(), e);
        }
    }

    /**
     * Check for overdue tasks every 30 minutes during business hours (9 AM - 6 PM)
     * Runs at 9:00, 9:30, 10:00, 10:30, etc. until 18:00
     */
    @Scheduled(cron = "0 0,30 9-18 * * MON-FRI")
    public void checkOverdueTasksBusinessHours() {
        log.info("Starting business hours overdue task check at {}", LocalDateTime.now());
        
        try {
            LocalDateTime currentTime = LocalDateTime.now();
            List<TaskEntity> overdueTasks = taskRepository.findOverdueTasks(currentTime);
            
            if (overdueTasks.isEmpty()) {
                log.debug("No overdue tasks found during business hours check");
                return;
            }
            
            log.info("Found {} overdue tasks during business hours", overdueTasks.size());
            
            // Process overdue tasks asynchronously
            asyncTaskService.processOverdueTasks(overdueTasks);
            
        } catch (Exception e) {
            log.error("Error during business hours overdue task check: {}", e.getMessage(), e);
        }
    }

    /**
     * Daily cleanup task - runs at 2 AM every day
     * Can be used for cleanup operations, archiving, etc.
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void dailyCleanup() {
        log.info("Starting daily cleanup task at {}", LocalDateTime.now());
        
        try {
            // This is a placeholder for future cleanup operations
            // For example: archiving old notifications, cleaning up temporary data, etc.
            log.info("Daily cleanup completed successfully");
            
        } catch (Exception e) {
            log.error("Error during daily cleanup: {}", e.getMessage(), e);
        }
    }
}
