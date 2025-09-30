package org.example.taskmanager.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.example.taskmanager.domain.NotificationEntity;
import org.example.taskmanager.domain.TaskEntity;
import org.example.taskmanager.domain.UserEntity;
import org.example.taskmanager.repository.NotificationDataRepository;
import org.example.taskmanager.repository.TaskDataRepository;
import org.example.taskmanager.repository.UserDataRepository;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AsyncTaskServiceTest {

    @Mock
    private TaskDataRepository taskRepository;

    @Mock
    private UserDataRepository userRepository;

    @Mock
    private NotificationDataRepository notificationRepository;

    @InjectMocks
    private AsyncTaskService asyncTaskService;

    private TaskEntity testTask;
    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        testUser = UserEntity.builder()
                .id(1L)
                .login("testuser")
                .passwordHash("hashedpassword")
                .emailAddress("test@example.com")
                .build();

        testTask = TaskEntity.builder()
                .id(1L)
                .name("Test Task")
                .details("Test Details")
                .dueDate(LocalDateTime.now().minusHours(1)) // Overdue task
                .ownerId(1L)
                .finished(false)
                .removed(false)
                .build();
    }

    @Test
    void processOverdueTasks_ShouldProcessAllTasks() {
        // Given
        List<TaskEntity> overdueTasks = Arrays.asList(testTask);
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(testUser));
        when(notificationRepository.save(any(NotificationEntity.class))).thenReturn(
            NotificationEntity.builder()
                .message("test message")
                .relatedTaskId(1L)
                .recipientId(1L)
                .timestamp(LocalDateTime.now())
                .viewed(false)
                .build()
        );

        // When
        asyncTaskService.processOverdueTasks(overdueTasks);

        // Then
        verify(userRepository, times(1)).findById(1L);
        verify(notificationRepository, times(1)).save(any(NotificationEntity.class));
    }

    @Test
    void processOverdueTasks_ShouldHandleEmptyList() {
        // Given
        List<TaskEntity> emptyList = Arrays.asList();

        // When
        asyncTaskService.processOverdueTasks(emptyList);

        // Then
        verify(userRepository, never()).findById(anyLong());
        verify(notificationRepository, never()).save(any(NotificationEntity.class));
    }

    @Test
    void processOverdueTasks_ShouldHandleUserNotFound() {
        // Given
        List<TaskEntity> overdueTasks = Arrays.asList(testTask);
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        // When
        asyncTaskService.processOverdueTasks(overdueTasks);

        // Then
        verify(userRepository, times(1)).findById(1L);
        verify(notificationRepository, never()).save(any(NotificationEntity.class));
    }

    @Test
    void createOverdueNotification_ShouldCreateNotification() {
        // Given
        when(notificationRepository.save(any(NotificationEntity.class))).thenReturn(
            NotificationEntity.builder()
                .message("test message")
                .relatedTaskId(1L)
                .recipientId(1L)
                .timestamp(LocalDateTime.now())
                .viewed(false)
                .build()
        );

        // When
        asyncTaskService.createOverdueNotification(testTask, testUser);

        // Then
        verify(notificationRepository, times(1)).save(argThat(notification -> 
            notification.getMessage().contains(testTask.getName()) &&
            notification.getRecipientId().equals(testUser.getId()) &&
            notification.getRelatedTaskId().equals(testTask.getId())
        ));
    }

    @Test
    void createOverdueNotification_ShouldHandleException() {
        // Given
        when(notificationRepository.save(any(NotificationEntity.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertDoesNotThrow(() -> asyncTaskService.createOverdueNotification(testTask, testUser));
        verify(notificationRepository, times(1)).save(any(NotificationEntity.class));
    }
}
