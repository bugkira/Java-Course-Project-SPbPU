package spbstu.TasksApplication.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spbstu.TasksApplication.model.Task;
import spbstu.TasksApplication.service.TaskService;
import spbstu.TasksApplication.exception.DuplicateResourceException;
import spbstu.TasksApplication.exception.ResourceNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Task>> getAllTasks(@PathVariable Long userId) {
        return ResponseEntity.ok(taskService.getAllTasks(userId));
    }

    @GetMapping("/user/{userId}/pending")
    public ResponseEntity<List<Task>> getPendingTasks(@PathVariable Long userId) {
        return ResponseEntity.ok(taskService.getPendingTasks(userId));
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        return ResponseEntity.ok(taskService.createTask(task));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<Task> updateTask(@PathVariable Long taskId, @RequestBody Task task) {
        return ResponseEntity.ok(taskService.updateTask(taskId, task));
    }

    @PutMapping("/{taskId}/complete")
    public ResponseEntity<Void> completeTask(@PathVariable Long taskId) {
        taskService.completeTask(taskId);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<String> handleDuplicateResourceException(DuplicateResourceException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}
