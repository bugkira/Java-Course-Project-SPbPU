package spbstu.TasksApplication.repository.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spbstu.TasksApplication.model.Task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskRepositoryTest {
    private InMemoryTaskRepository taskRepository;
    private Task testTask;

    @BeforeEach
    void setUp() {
        taskRepository = new InMemoryTaskRepository();
        testTask = Task.builder()
                .title("Test Task")
                .description("Test Description")
                .targetDate(LocalDateTime.now().plusDays(1))
                .userId(1L)
                .build();
    }

    @Test
    void save_ShouldCreateNewTask_WhenTaskIdIsNull() {
        Task savedTask = taskRepository.save(testTask);
        
        assertNotNull(savedTask.getTaskId());
        assertEquals(testTask.getTitle(), savedTask.getTitle());
        assertEquals(testTask.getUserId(), savedTask.getUserId());
    }

    @Test
    void save_ShouldUpdateExistingTask_WhenTaskIdIsNotNull() {
        Task savedTask = taskRepository.save(testTask);
        savedTask.setTitle("Updated Title");
        
        Task updatedTask = taskRepository.save(savedTask);
        
        assertEquals(savedTask.getTaskId(), updatedTask.getTaskId());
        assertEquals("Updated Title", updatedTask.getTitle());
    }

    @Test
    void findByUserIdAndIsDeletedFalse_ShouldReturnOnlyNotDeletedTasks() {
        Task task1 = taskRepository.save(testTask);
        Task task2 = Task.builder()
                .title("Deleted Task")
                .description("Description")
                .targetDate(LocalDateTime.now().plusDays(1))
                .userId(1L)
                .build();
        Task task3 = Task.builder()
                .title("Another User Task")
                .description("Description")
                .targetDate(LocalDateTime.now().plusDays(1))
                .userId(2L)
                .build();
        
        taskRepository.save(task2);
        taskRepository.save(task3);
        task2.setIsDeleted(true);
        taskRepository.save(task2);
        
        List<Task> tasks = taskRepository.findByUserIdAndIsDeletedFalse(1L);
        
        assertEquals(1, tasks.size());
        assertEquals(task1.getTaskId(), tasks.get(0).getTaskId());
    }

    @Test
    void findByUserIdAndIsCompletedFalseAndIsDeletedFalse_ShouldReturnOnlyActiveTasks() {
        Task activeTask = taskRepository.save(testTask);
        Task completedTask = Task.builder()
                .title("Completed Task")
                .description("Description")
                .targetDate(LocalDateTime.now().plusDays(1))
                .userId(1L)
                .build();
        Task deletedTask = Task.builder()
                .title("Deleted Task")
                .description("Description")
                .targetDate(LocalDateTime.now().plusDays(1))
                .userId(1L)
                .build();
        
        taskRepository.save(completedTask);
        taskRepository.save(deletedTask);
        completedTask.setIsCompleted(true);
        deletedTask.setIsDeleted(true);
        taskRepository.save(completedTask);
        taskRepository.save(deletedTask);
        
        List<Task> tasks = taskRepository.findByUserIdAndIsCompletedFalseAndIsDeletedFalse(1L);
        
        assertEquals(1, tasks.size());
        assertEquals(activeTask.getTaskId(), tasks.get(0).getTaskId());
    }

    @Test
    void findByIdAndDeletedFalse_ShouldReturnTask_WhenExistsAndNotDeleted() {
        Task savedTask = taskRepository.save(testTask);
        Optional<Task> foundTask = taskRepository.findByIdAndDeletedFalse(savedTask.getTaskId());
        
        assertTrue(foundTask.isPresent());
        assertEquals(savedTask.getTaskId(), foundTask.get().getTaskId());
    }

    @Test
    void findByIdAndDeletedFalse_ShouldReturnEmpty_WhenTaskIsDeleted() {
        Task savedTask = taskRepository.save(testTask);
        savedTask.setIsDeleted(true);
        taskRepository.save(savedTask);
        
        Optional<Task> foundTask = taskRepository.findByIdAndDeletedFalse(savedTask.getTaskId());
        
        assertTrue(foundTask.isEmpty());
    }

    @Test
    void findByIdAndDeletedFalse_ShouldReturnEmpty_WhenTaskDoesNotExist() {
        Optional<Task> foundTask = taskRepository.findByIdAndDeletedFalse(999L);
        
        assertTrue(foundTask.isEmpty());
    }
}
