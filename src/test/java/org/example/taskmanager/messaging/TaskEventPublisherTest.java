package org.example.taskmanager.messaging;

import java.time.LocalDateTime;
import java.util.Optional;

import org.example.taskmanager.domain.TaskEntity;
import org.example.taskmanager.domain.UserEntity;
import org.example.taskmanager.repository.UserDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@ExtendWith(MockitoExtension.class)
class TaskEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private UserDataRepository userRepository;

    @InjectMocks
    private TaskEventPublisher taskEventPublisher;

    private TaskEntity testTask;
    private UserEntity testUser;

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
                .createTime(LocalDateTime.now())
                .dueDate(LocalDateTime.now().plusDays(1))
                .removed(false)
                .build();
    }

    @Test
    void shouldPublishTaskCreatedEvent() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        taskEventPublisher.publishTaskCreated(testTask);

        // Then
        verify(rabbitTemplate).convertAndSend(
                eq("task.exchange"),
                eq("task.created"),
                any(TaskEventMessage.class)
        );
    }

    @Test
    void shouldPublishTaskUpdatedEvent() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        taskEventPublisher.publishTaskUpdated(testTask);

        // Then
        verify(rabbitTemplate).convertAndSend(
                eq("task.exchange"),
                eq("task.updated"),
                any(TaskEventMessage.class)
        );
    }

    @Test
    void shouldPublishTaskDeletedEvent() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        taskEventPublisher.publishTaskDeleted(1L, "Test Task", 1L);

        // Then
        verify(rabbitTemplate).convertAndSend(
                eq("task.exchange"),
                eq("task.deleted"),
                any(TaskEventMessage.class)
        );
    }

    @Test
    void shouldHandleUserNotFoundGracefully() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        taskEventPublisher.publishTaskCreated(testTask);

        // Then
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(Object.class));
    }
}
