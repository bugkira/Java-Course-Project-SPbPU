package spbstu.TasksApplication.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import spbstu.TasksApplication.model.Task;
import spbstu.TasksApplication.repository.TaskRepository;
import spbstu.TasksApplication.service.TaskSchedulerService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskSchedulerServiceImpl implements TaskSchedulerService {

    private final TaskRepository taskRepository;

    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void checkOverdueTasks() {
        log.info("Checking for overdue tasks...");
        List<Task> overdueTasks = findOverdueTasks();
        
        for (Task task : overdueTasks) {
            processOverdueTask(task);
        }
        
        log.info("Found {} overdue tasks", overdueTasks.size());
    }

    @Override
    @Async
    public void processOverdueTask(Task task) {
        log.info("Processing overdue task: {} for user: {}", task.getTitle(), task.getUserId());
        
        // Log overdue task for monitoring
        log.warn("Task '{}' is overdue! Target date: {}", task.getTitle(), task.getTargetDate());
    }

    @Override
    public List<Task> findOverdueTasks() {
        LocalDateTime now = LocalDateTime.now();
        return taskRepository.findByUserIdAndIsCompletedFalseAndIsDeletedFalse(null)
                .stream()
                .filter(task -> task.getTargetDate().isBefore(now))
                .toList();
    }
}
