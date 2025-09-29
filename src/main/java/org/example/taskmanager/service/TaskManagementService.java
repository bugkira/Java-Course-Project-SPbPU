package org.example.taskmanager.service;

import java.time.LocalDateTime;
import java.util.List;

import org.example.taskmanager.domain.TaskEntity;
import org.example.taskmanager.exception.EntityNotFoundException;
import org.example.taskmanager.messaging.TaskEventPublisher;
import org.example.taskmanager.repository.TaskDataRepository;
import org.example.taskmanager.repository.UserDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskManagementService {

    private final TaskDataRepository taskRepository;
    private final UserDataRepository userRepository;
    
    @Autowired(required = false)
    private TaskEventPublisher taskEventPublisher;

    @Cacheable(value = "tasks", key = "'all-tasks-' + #ownerId")
    public List<TaskEntity> getAllUserTasks(Long ownerId) {
        return taskRepository.findAllByOwnerId(ownerId);
    }

    @Cacheable(value = "tasks", key = "'active-tasks-' + #ownerId")
    public List<TaskEntity> getActiveTasks(Long ownerId) {
        return taskRepository.findPendingTasksByOwnerId(ownerId);
    }

    @Cacheable(value = "tasks", key = "'task-' + #taskId + '-' + #ownerId")
    public TaskEntity getTaskDetails(Long taskId, Long ownerId) {
        return taskRepository.findByIdAndOwnerId(taskId, ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Task with id " + taskId + " not found"));
    }

    @CacheEvict(value = "tasks", allEntries = true)
    public TaskEntity addNewTask(TaskEntity task) {
        performTaskValidation(task);
        task.setCreateTime(LocalDateTime.now());
        TaskEntity savedTask = taskRepository.save(task);
        
        // Publish task created event
        if (taskEventPublisher != null) {
            taskEventPublisher.publishTaskCreated(savedTask);
        }
        
        return savedTask;
    }

    @CacheEvict(value = "tasks", allEntries = true)
    public void removeTask(Long taskId, Long ownerId) {
        TaskEntity task = taskRepository.findByIdAndOwnerId(taskId, ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Task with id " + taskId + " not found"));
        
        // Store task info before marking as removed
        String taskName = task.getName();
        Long taskOwnerId = task.getOwnerId();
        
        task.setRemoved(true);
        taskRepository.save(task);
        
        // Publish task deleted event
        if (taskEventPublisher != null) {
            taskEventPublisher.publishTaskDeleted(taskId, taskName, taskOwnerId);
        }
    }

    private void performTaskValidation(TaskEntity task) {
        if (task.getOwnerId() == null) {
            throw new IllegalArgumentException("Task owner ID must not be null");
        }
        if (!userRepository.existsById(task.getOwnerId())) {
            throw new IllegalArgumentException("User with id " + task.getOwnerId() + " does not exist");
        }
        if (task.getName() == null || task.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Task name must not be empty");
        }
        if (task.getDetails() == null || task.getDetails().trim().isEmpty()) {
            throw new IllegalArgumentException("Task details must not be empty");
        }
        if (task.getDueDate() == null) {
            throw new IllegalArgumentException("Task due date must not be null");
        }
        if (task.getDueDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Task due date cannot be in the past");
        }
    }
}
