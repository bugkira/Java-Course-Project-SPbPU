package spbstu.TasksApplication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spbstu.TasksApplication.exception.ResourceNotFoundException;
import spbstu.TasksApplication.messaging.TaskMessagePublisher;
import spbstu.TasksApplication.model.Task;
import spbstu.TasksApplication.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMessagePublisher taskMessagePublisher;

    public List<Task> getAllTasks(Long userId) {
        return taskRepository.findByUserIdAndIsDeletedFalse(userId);
    }

    public List<Task> getPendingTasks(Long userId) {
        return taskRepository.findByUserIdAndIsCompletedFalseAndIsDeletedFalse(userId);
    }

    public Task getTaskById(Long taskId) {
        return taskRepository.findByIdAndDeletedFalse(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
    }

    public Task createTask(Task task) {
        validateTask(task);
        task.setCreationDate(LocalDateTime.now());
        Task savedTask = taskRepository.save(task);
        taskMessagePublisher.publishTaskCreated(savedTask);
        return savedTask;
    }

    public void deleteTask(Long taskId) {
        Task task = taskRepository.findByIdAndDeletedFalse(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
        task.setIsDeleted(true);
        taskRepository.save(task);
    }

    public Task updateTask(Long taskId, Task updatedTask) {
        Task existingTask = taskRepository.findByIdAndDeletedFalse(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
        
        validateTask(updatedTask);
        
        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setTargetDate(updatedTask.getTargetDate());
        
        return taskRepository.save(existingTask);
    }

    public void completeTask(Long taskId) {
        Task task = taskRepository.findByIdAndDeletedFalse(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
        task.setIsCompleted(true);
        taskRepository.save(task);
    }

    private void validateTask(Task task) {
        if (task.getUserId() == null) {
            throw new IllegalArgumentException("Task userId cannot be null");
        }
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be empty");
        }
        if (task.getDescription() == null || task.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Task description cannot be empty");
        }
        if (task.getTargetDate() == null) {
            throw new IllegalArgumentException("Task target date cannot be null");
        }
        if (task.getTargetDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Task target date cannot be in the past");
        }
    }
}
