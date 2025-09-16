package spbstu.TasksApplication.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spbstu.TasksApplication.exception.ResourceNotFoundException;
import spbstu.TasksApplication.messaging.TaskMessagePublisher;
import spbstu.TasksApplication.model.Task;
import spbstu.TasksApplication.repository.impl.InMemoryTaskRepository;
import spbstu.TasksApplication.service.TaskService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    private TaskService taskService;
    private Task testTask;
    
    @Mock
    private TaskMessagePublisher taskMessagePublisher;

    @BeforeEach
    void setUp() {
        taskService = new TaskService(new InMemoryTaskRepository(), taskMessagePublisher);
        testTask = Task.builder()
                .title("Test Task")
                .description("Test Description")
                .targetDate(LocalDateTime.now().plusDays(1))
                .userId(1L)
                .build();
    }

    @Test
    void createTask_ShouldCreateNewTask() {
        Task createdTask = taskService.createTask(testTask);
        
        assertNotNull(createdTask.getTaskId());
        assertEquals(testTask.getTitle(), createdTask.getTitle());
        assertEquals(testTask.getDescription(), createdTask.getDescription());
        assertEquals(testTask.getUserId(), createdTask.getUserId());
        assertNotNull(createdTask.getCreationDate());
    }

    @Test
    void createTask_ShouldThrowException_WhenTitleIsEmpty() {
        testTask.setTitle("");
        
        assertThrows(IllegalArgumentException.class, () -> taskService.createTask(testTask));
    }

    @Test
    void getTaskById_ShouldReturnTask_WhenExists() {
        Task createdTask = taskService.createTask(testTask);
        Task foundTask = taskService.getTaskById(createdTask.getTaskId());
        
        assertEquals(createdTask.getTaskId(), foundTask.getTaskId());
    }

    @Test
    void getTaskById_ShouldThrowException_WhenNotExists() {
        assertThrows(ResourceNotFoundException.class, () -> taskService.getTaskById(999L));
    }

    @Test
    void getAllTasks_ShouldReturnOnlyNotDeletedTasks() {
        Task task1 = taskService.createTask(testTask);
        Task task2 = Task.builder()
                .title("Deleted Task")
                .description("Description")
                .targetDate(LocalDateTime.now().plusDays(1))
                .userId(1L)
                .build();
        taskService.createTask(task2);
        taskService.deleteTask(task2.getTaskId());

        List<Task> tasks = taskService.getAllTasks(1L);
        
        assertEquals(1, tasks.size());
        assertEquals(task1.getTaskId(), tasks.get(0).getTaskId());
    }

    @Test
    void getPendingTasks_ShouldReturnOnlyActiveTasks() {
        Task activeTask = taskService.createTask(testTask);
        Task completedTask = Task.builder()
                .title("Completed Task")
                .description("Description")
                .targetDate(LocalDateTime.now().plusDays(1))
                .userId(1L)
                .build();
        taskService.createTask(completedTask);
        taskService.completeTask(completedTask.getTaskId());

        List<Task> tasks = taskService.getPendingTasks(1L);
        
        assertEquals(1, tasks.size());
        assertEquals(activeTask.getTaskId(), tasks.get(0).getTaskId());
    }

    @Test
    void updateTask_ShouldUpdateTask() {
        Task createdTask = taskService.createTask(testTask);
        Task updatedTask = Task.builder()
                .title("Updated Title")
                .description("Updated Description")
                .targetDate(LocalDateTime.now().plusDays(2))
                .userId(1L)
                .build();

        Task result = taskService.updateTask(createdTask.getTaskId(), updatedTask);
        
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Description", result.getDescription());
    }

    @Test
    void completeTask_ShouldMarkTaskAsCompleted() {
        Task createdTask = taskService.createTask(testTask);
        taskService.completeTask(createdTask.getTaskId());
        
        Task completedTask = taskService.getTaskById(createdTask.getTaskId());
        assertTrue(completedTask.getIsCompleted());
    }
}
