package org.example.taskmanager.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.example.taskmanager.domain.TaskEntity;
import org.example.taskmanager.exception.EntityNotFoundException;
import org.example.taskmanager.repository.TaskDataRepository;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TaskManagementServiceTest {

    @Mock
    private TaskDataRepository taskRepository;

    @InjectMocks
    private TaskManagementService taskService;

    private TaskEntity validTask;
    private TaskEntity completedTask;
    private TaskEntity removedTask;
    private Long testOwnerId;
    private Long testTaskId;

    @BeforeEach
    void setUp() {
        testOwnerId = 1L;
        testTaskId = 1L;
        
        validTask = TaskEntity.builder()
                .id(testTaskId)
                .name("Test Task")
                .details("Test task details")
                .ownerId(testOwnerId)
                .dueDate(LocalDateTime.now().plusDays(1))
                .finished(false)
                .removed(false)
                .build();

        completedTask = TaskEntity.builder()
                .id(2L)
                .name("Completed Task")
                .details("Completed task details")
                .ownerId(testOwnerId)
                .dueDate(LocalDateTime.now().plusDays(1))
                .finished(true)
                .removed(false)
                .build();

        removedTask = TaskEntity.builder()
                .id(3L)
                .name("Removed Task")
                .details("Removed task details")
                .ownerId(testOwnerId)
                .dueDate(LocalDateTime.now().plusDays(1))
                .finished(false)
                .removed(true)
                .build();
    }

    @Test
    void testGetAllUserTasks_Success() {
        // Given
        List<TaskEntity> expectedTasks = Arrays.asList(validTask, completedTask);
        when(taskRepository.findAllByOwnerAndNotRemoved(testOwnerId)).thenReturn(expectedTasks);

        // When
        List<TaskEntity> actualTasks = taskService.getAllUserTasks(testOwnerId);

        // Then
        assertEquals(expectedTasks, actualTasks);
        verify(taskRepository).findAllByOwnerAndNotRemoved(testOwnerId);
    }

    @Test
    void testGetAllUserTasks_EmptyResult() {
        // Given
        when(taskRepository.findAllByOwnerAndNotRemoved(testOwnerId)).thenReturn(Collections.emptyList());

        // When
        List<TaskEntity> actualTasks = taskService.getAllUserTasks(testOwnerId);

        // Then
        assertTrue(actualTasks.isEmpty());
        verify(taskRepository).findAllByOwnerAndNotRemoved(testOwnerId);
    }

    @Test
    void testGetActiveTasks_Success() {
        // Given
        List<TaskEntity> expectedTasks = Collections.singletonList(validTask);
        when(taskRepository.findPendingByOwnerAndNotRemoved(testOwnerId)).thenReturn(expectedTasks);

        // When
        List<TaskEntity> actualTasks = taskService.getActiveTasks(testOwnerId);

        // Then
        assertEquals(expectedTasks, actualTasks);
        verify(taskRepository).findPendingByOwnerAndNotRemoved(testOwnerId);
    }

    @Test
    void testGetActiveTasks_EmptyResult() {
        // Given
        when(taskRepository.findPendingByOwnerAndNotRemoved(testOwnerId)).thenReturn(Collections.emptyList());

        // When
        List<TaskEntity> actualTasks = taskService.getActiveTasks(testOwnerId);

        // Then
        assertTrue(actualTasks.isEmpty());
        verify(taskRepository).findPendingByOwnerAndNotRemoved(testOwnerId);
    }

    @Test
    void testGetTaskDetails_Success() {
        // Given
        when(taskRepository.findByIdAndNotRemoved(testTaskId)).thenReturn(Optional.of(validTask));

        // When
        TaskEntity actualTask = taskService.getTaskDetails(testTaskId);

        // Then
        assertEquals(validTask, actualTask);
        verify(taskRepository).findByIdAndNotRemoved(testTaskId);
    }

    @Test
    void testGetTaskDetails_TaskNotFound() {
        // Given
        when(taskRepository.findByIdAndNotRemoved(testTaskId)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, 
            () -> taskService.getTaskDetails(testTaskId));
        
        assertEquals("Task with id " + testTaskId + " not found", exception.getMessage());
        verify(taskRepository).findByIdAndNotRemoved(testTaskId);
    }

    @Test
    void testAddNewTask_Success() {
        // Given
        TaskEntity newTask = TaskEntity.builder()
                .name("New Task")
                .details("New task details")
                .ownerId(testOwnerId)
                .dueDate(LocalDateTime.now().plusDays(1))
                .build();
        
        TaskEntity savedTask = TaskEntity.builder()
                .id(testTaskId)
                .name("New Task")
                .details("New task details")
                .ownerId(testOwnerId)
                .dueDate(LocalDateTime.now().plusDays(1))
                .finished(false)
                .removed(false)
                .build();

        when(taskRepository.store(any(TaskEntity.class))).thenReturn(savedTask);

        // When
        TaskEntity result = taskService.addNewTask(newTask);

        // Then
        assertNotNull(result);
        assertEquals(testTaskId, result.getId());
        assertEquals("New Task", result.getName());
        assertNotNull(result.getCreateTime());
        verify(taskRepository).store(any(TaskEntity.class));
    }

    @Test
    void testAddNewTask_ValidationError_EmptyName() {
        // Given
        TaskEntity invalidTask = TaskEntity.builder()
                .name("")
                .details("Test details")
                .ownerId(testOwnerId)
                .dueDate(LocalDateTime.now().plusDays(1))
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> taskService.addNewTask(invalidTask));
        
        assertEquals("Task name must not be empty", exception.getMessage());
        verify(taskRepository, never()).store(any(TaskEntity.class));
    }

    @Test
    void testAddNewTask_ValidationError_EmptyDetails() {
        // Given
        TaskEntity invalidTask = TaskEntity.builder()
                .name("Test Task")
                .details("")
                .ownerId(testOwnerId)
                .dueDate(LocalDateTime.now().plusDays(1))
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> taskService.addNewTask(invalidTask));
        
        assertEquals("Task details must not be empty", exception.getMessage());
        verify(taskRepository, never()).store(any(TaskEntity.class));
    }

    @Test
    void testAddNewTask_ValidationError_PastDueDate() {
        // Given
        TaskEntity invalidTask = TaskEntity.builder()
                .name("Test Task")
                .details("Test details")
                .ownerId(testOwnerId)
                .dueDate(LocalDateTime.now().minusDays(1))
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> taskService.addNewTask(invalidTask));
        
        assertEquals("Task due date cannot be in the past", exception.getMessage());
        verify(taskRepository, never()).store(any(TaskEntity.class));
    }

    @Test
    void testRemoveTask_Success() {
        // Given
        when(taskRepository.findByIdAndNotRemoved(testTaskId)).thenReturn(Optional.of(validTask));
        when(taskRepository.store(any(TaskEntity.class))).thenReturn(validTask);

        // When
        taskService.removeTask(testTaskId);

        // Then
        verify(taskRepository).findByIdAndNotRemoved(testTaskId);
        verify(taskRepository).store(any(TaskEntity.class));
    }

    @Test
    void testRemoveTask_TaskNotFound() {
        // Given
        when(taskRepository.findByIdAndNotRemoved(testTaskId)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
            () -> taskService.removeTask(testTaskId));
        
        assertEquals("Task with id " + testTaskId + " not found", exception.getMessage());
        verify(taskRepository).findByIdAndNotRemoved(testTaskId);
        verify(taskRepository, never()).store(any(TaskEntity.class));
    }
}