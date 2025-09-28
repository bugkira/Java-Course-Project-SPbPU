package org.example.taskmanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.example.taskmanager.exception.EntityNotFoundException;
import org.example.taskmanager.domain.TaskEntity;
import org.example.taskmanager.repository.TaskDataRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskManagementService {

    private final TaskDataRepository taskRepository;

    public List<TaskEntity> getAllUserTasks(Long ownerId) {
        return taskRepository.findAllByOwnerAndNotRemoved(ownerId);
    }

    public List<TaskEntity> getActiveTasks(Long ownerId) {
        return taskRepository.findPendingByOwnerAndNotRemoved(ownerId);
    }

    public TaskEntity getTaskDetails(Long taskId) {
        return taskRepository.findByIdAndNotRemoved(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task with id " + taskId + " not found"));
    }

    public TaskEntity addNewTask(TaskEntity task) {
        performTaskValidation(task);
        task.setCreateTime(LocalDateTime.now());
        return taskRepository.store(task);
    }

    public void removeTask(Long taskId) {
        TaskEntity task = taskRepository.findByIdAndNotRemoved(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task with id " + taskId + " not found"));
        task.setRemoved(true);
        taskRepository.store(task);
    }

    private void performTaskValidation(TaskEntity task) {
        if (task.getOwnerId() == null) {
            throw new IllegalArgumentException("Task owner ID must not be null");
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
