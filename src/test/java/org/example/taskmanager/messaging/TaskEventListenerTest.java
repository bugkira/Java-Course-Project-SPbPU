package org.example.taskmanager.messaging;

import org.example.taskmanager.domain.NotificationEntity;
import org.example.taskmanager.repository.NotificationDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskEventListenerTest {

    @Mock
    private NotificationDataRepository notificationRepository;

    @InjectMocks
    private TaskEventListener taskEventListener;

    private TaskEventMessage testMessage;

    @BeforeEach
    void setUp() {
        testMessage = TaskEventMessage.builder()
                .taskId(1L)
                .taskName("Test Task")
                .taskDetails("Test task details")
                .ownerId(1L)
                .ownerLogin("testuser")
                .ownerEmail("test@example.com")
                .createTime(LocalDateTime.now())
                .dueDate(LocalDateTime.now().plusDays(1))
                .eventType("CREATED")
                .eventTime(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldHandleTaskCreatedEvent() {
        // When
        taskEventListener.handleTaskCreated(testMessage);

        // Then
        verify(notificationRepository).save(any(NotificationEntity.class));
    }

    @Test
    void shouldHandleTaskUpdatedEvent() {
        // When
        taskEventListener.handleTaskUpdated(testMessage);

        // Then
        verify(notificationRepository).save(any(NotificationEntity.class));
    }

    @Test
    void shouldHandleTaskDeletedEvent() {
        // When
        taskEventListener.handleTaskDeleted(testMessage);

        // Then
        verify(notificationRepository).save(any(NotificationEntity.class));
    }

    @Test
    void shouldCreateCorrectNotificationForTaskCreated() {
        // When
        taskEventListener.handleTaskCreated(testMessage);

        // Then
        verify(notificationRepository).save(argThat(notification -> 
                notification.getMessage().equals("New task created: Test Task") &&
                notification.getRelatedTaskId().equals(1L) &&
                notification.getRecipientId().equals(1L) &&
                !notification.getViewed()
        ));
    }

    @Test
    void shouldCreateCorrectNotificationForTaskUpdated() {
        // When
        taskEventListener.handleTaskUpdated(testMessage);

        // Then
        verify(notificationRepository).save(argThat(notification -> 
                notification.getMessage().equals("Task updated: Test Task") &&
                notification.getRelatedTaskId().equals(1L) &&
                notification.getRecipientId().equals(1L) &&
                !notification.getViewed()
        ));
    }

    @Test
    void shouldCreateCorrectNotificationForTaskDeleted() {
        // When
        taskEventListener.handleTaskDeleted(testMessage);

        // Then
        verify(notificationRepository).save(argThat(notification -> 
                notification.getMessage().equals("Task deleted: Test Task") &&
                notification.getRelatedTaskId().equals(1L) &&
                notification.getRecipientId().equals(1L) &&
                !notification.getViewed()
        ));
    }
}
