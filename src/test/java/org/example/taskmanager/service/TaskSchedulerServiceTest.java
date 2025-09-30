package org.example.taskmanager.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.example.taskmanager.domain.TaskEntity;
import org.example.taskmanager.repository.TaskDataRepository;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TaskSchedulerServiceTest {

    @Mock
    private TaskDataRepository taskRepository;

    @Mock
    private AsyncTaskService asyncTaskService;

    @InjectMocks
    private TaskSchedulerService taskSchedulerService;

    private TaskEntity overdueTask;

    @BeforeEach
    void setUp() {
        overdueTask = TaskEntity.builder()
                .id(1L)
                .name("Overdue Task")
                .details("This task is overdue")
                .dueDate(LocalDateTime.now().minusHours(1))
                .ownerId(1L)
                .finished(false)
                .removed(false)
                .build();
    }

    @Test
    void checkOverdueTasks_ShouldProcessOverdueTasks() {
        // Given
        List<TaskEntity> overdueTasks = Arrays.asList(overdueTask);
        when(taskRepository.findOverdueTasks(any(LocalDateTime.class))).thenReturn(overdueTasks);

        // When
        taskSchedulerService.checkOverdueTasks();

        // Then
        verify(taskRepository, times(1)).findOverdueTasks(any(LocalDateTime.class));
        verify(asyncTaskService, times(1)).processOverdueTasks(overdueTasks);
    }

    @Test
    void checkOverdueTasks_ShouldHandleNoOverdueTasks() {
        // Given
        when(taskRepository.findOverdueTasks(any(LocalDateTime.class))).thenReturn(Collections.emptyList());

        // When
        taskSchedulerService.checkOverdueTasks();

        // Then
        verify(taskRepository, times(1)).findOverdueTasks(any(LocalDateTime.class));
        verify(asyncTaskService, never()).processOverdueTasks(any());
    }

    @Test
    void checkOverdueTasks_ShouldHandleException() {
        // Given
        when(taskRepository.findOverdueTasks(any(LocalDateTime.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertDoesNotThrow(() -> taskSchedulerService.checkOverdueTasks());
        verify(taskRepository, times(1)).findOverdueTasks(any(LocalDateTime.class));
        verify(asyncTaskService, never()).processOverdueTasks(any());
    }

    @Test
    void checkOverdueTasksBusinessHours_ShouldProcessOverdueTasks() {
        // Given
        List<TaskEntity> overdueTasks = Arrays.asList(overdueTask);
        when(taskRepository.findOverdueTasks(any(LocalDateTime.class))).thenReturn(overdueTasks);

        // When
        taskSchedulerService.checkOverdueTasksBusinessHours();

        // Then
        verify(taskRepository, times(1)).findOverdueTasks(any(LocalDateTime.class));
        verify(asyncTaskService, times(1)).processOverdueTasks(overdueTasks);
    }

    @Test
    void checkOverdueTasksBusinessHours_ShouldHandleNoOverdueTasks() {
        // Given
        when(taskRepository.findOverdueTasks(any(LocalDateTime.class))).thenReturn(Collections.emptyList());

        // When
        taskSchedulerService.checkOverdueTasksBusinessHours();

        // Then
        verify(taskRepository, times(1)).findOverdueTasks(any(LocalDateTime.class));
        verify(asyncTaskService, never()).processOverdueTasks(any());
    }

    @Test
    void dailyCleanup_ShouldCompleteSuccessfully() {
        // Given - no setup needed for cleanup
        
        // When & Then
        assertDoesNotThrow(() -> taskSchedulerService.dailyCleanup());
        
        // Verify no interactions with repositories during cleanup
        verifyNoInteractions(taskRepository);
        verifyNoInteractions(asyncTaskService);
    }
}
