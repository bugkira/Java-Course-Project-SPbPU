package org.example.taskmanager.service;

import org.example.taskmanager.domain.NotificationEntity;
import org.example.taskmanager.domain.TaskEntity;
import org.example.taskmanager.domain.UserEntity;
import org.example.taskmanager.exception.DuplicateEntityException;
import org.example.taskmanager.exception.EntityAlreadyExistsException;
import org.example.taskmanager.exception.EntityNotFoundException;
import org.example.taskmanager.repository.NotificationDataRepository;
import org.example.taskmanager.repository.TaskDataRepository;
import org.example.taskmanager.repository.UserDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MockitoRepositoryIntegrationTest {

    @Mock
    private UserDataRepository userRepository;

    @Mock
    private TaskDataRepository taskRepository;

    @Mock
    private NotificationDataRepository notificationRepository;

    @InjectMocks
    private UserManagementService userService;

    @InjectMocks
    private TaskManagementService taskService;

    @InjectMocks
    private NotificationManagementService notificationService;

    private UserEntity testUser;
    private TaskEntity testTask;
    private NotificationEntity testNotification;

    @BeforeEach
    void setUp() {
        testUser = UserEntity.builder()
                .id(1L)
                .login("testuser")
                .emailAddress("test@example.com")
                .passwordHash("password123")
                .build();

        testTask = TaskEntity.builder()
                .id(1L)
                .name("Test Task")
                .details("Test task details")
                .ownerId(1L)
                .dueDate(LocalDateTime.now().plusDays(1))
                .finished(false)
                .removed(false)
                .build();

        testNotification = NotificationEntity.builder()
                .id(1L)
                .message("Test notification")
                .relatedTaskId(1L)
                .recipientId(1L)
                .viewed(false)
                .build();
    }

    @Test
    void shouldCreateUserWhenRepositoryReturnsSavedUser() {
        // Given
        when(userRepository.existsByLogin(anyString())).thenReturn(false);
        when(userRepository.existsByEmailAddress(anyString())).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenReturn(testUser);

        // When
        UserEntity result = userService.createUserAccount(testUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getLogin()).isEqualTo("testuser");
        assertThat(result.getEmailAddress()).isEqualTo("test@example.com");
        verify(userRepository).existsByLogin("testuser");
        verify(userRepository).existsByEmailAddress("test@example.com");
        verify(userRepository).save(testUser);
    }

    @Test
    void shouldThrowExceptionWhenUserAlreadyExists() {
        // Given
        when(userRepository.existsByLogin(anyString())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.createUserAccount(testUser))
                .isInstanceOf(DuplicateEntityException.class)
                .hasMessageContaining("Login already exists");

        verify(userRepository).existsByLogin("testuser");
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void shouldAuthenticateUserWhenRepositoryReturnsUser() {
        // Given
        when(userRepository.findByLogin(anyString())).thenReturn(Optional.of(testUser));

        // When
        UserEntity result = userService.authenticateUser("testuser", "password123");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getLogin()).isEqualTo("testuser");
        verify(userRepository).findByLogin("testuser");
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findByLogin(anyString())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.authenticateUser("nonexistent", "password"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Invalid login or password");

        verify(userRepository).findByLogin("nonexistent");
    }

    @Test
    void shouldCreateTaskWhenRepositoryReturnsSavedTask() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(testTask);

        // When
        TaskEntity result = taskService.addNewTask(testTask);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Task");
        assertThat(result.getOwnerId()).isEqualTo(1L);
        verify(taskRepository).save(testTask);
    }

    @Test
    void shouldGetAllUserTasksWhenRepositoryReturnsTasks() {
        // Given
        List<TaskEntity> expectedTasks = Arrays.asList(testTask);
        when(taskRepository.findAllByOwnerId(anyLong())).thenReturn(expectedTasks);

        // When
        List<TaskEntity> result = taskService.getAllUserTasks(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Task");
        verify(taskRepository).findAllByOwnerId(1L);
    }

    @Test
    void shouldGetEmptyListWhenRepositoryReturnsEmptyList() {
        // Given
        when(taskRepository.findAllByOwnerId(anyLong())).thenReturn(Collections.emptyList());

        // When
        List<TaskEntity> result = taskService.getAllUserTasks(1L);

        // Then
        assertThat(result).isEmpty();
        verify(taskRepository).findAllByOwnerId(1L);
    }

    @Test
    void shouldGetAllUserNotificationsWhenRepositoryReturnsNotifications() {
        // Given
        List<NotificationEntity> expectedNotifications = Arrays.asList(testNotification);
        when(notificationRepository.findAllByRecipientId(anyLong())).thenReturn(expectedNotifications);

        // When
        List<NotificationEntity> result = notificationService.getAllUserNotifications(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMessage()).isEqualTo("Test notification");
        verify(notificationRepository).findAllByRecipientId(1L);
    }

    @Test
    void shouldGetEmptyListWhenRepositoryReturnsEmptyNotifications() {
        // Given
        when(notificationRepository.findAllByRecipientId(anyLong())).thenReturn(Collections.emptyList());

        // When
        List<NotificationEntity> result = notificationService.getAllUserNotifications(1L);

        // Then
        assertThat(result).isEmpty();
        verify(notificationRepository).findAllByRecipientId(1L);
    }
}
