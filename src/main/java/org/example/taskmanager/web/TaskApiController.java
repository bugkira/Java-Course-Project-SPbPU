package org.example.taskmanager.web;

import java.util.List;

import org.example.taskmanager.domain.TaskEntity;
import org.example.taskmanager.exception.EntityAlreadyExistsException;
import org.example.taskmanager.exception.EntityNotFoundException;
import org.example.taskmanager.service.TaskManagementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
public class TaskApiController {
    private final TaskManagementService taskService;

    public TaskApiController(TaskManagementService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/user/{ownerId}")
    public ResponseEntity<List<TaskEntity>> retrieveAllTasks(@PathVariable Long ownerId) {
        return ResponseEntity.ok(taskService.getAllUserTasks(ownerId));
    }

    @GetMapping("/user/{ownerId}/active")
    public ResponseEntity<List<TaskEntity>> retrieveActiveTasks(@PathVariable Long ownerId) {
        return ResponseEntity.ok(taskService.getActiveTasks(ownerId));
    }

    @PostMapping
    public ResponseEntity<TaskEntity> addTask(@RequestBody TaskEntity task) {
        return ResponseEntity.ok(taskService.addNewTask(task));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> removeTask(@PathVariable Long taskId) {
        taskService.removeTask(taskId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleValidationError(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<String> handleDuplicateEntity(EntityAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFound(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}
