package org.example.taskmanager.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.example.taskmanager.domain.NotificationEntity;
import org.example.taskmanager.repository.NotificationDataRepository;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationManagementServiceTest {

    @Mock
    private NotificationDataRepository notificationRepository;

    @InjectMocks
    private NotificationManagementService notificationService;

    private NotificationEntity notification1;
    private NotificationEntity notification2;
    private NotificationEntity unreadNotification;
    private Long testRecipientId;
    private Long testNotificationId;

    @BeforeEach
    void setUp() {
        testRecipientId = 1L;
        testNotificationId = 1L;

        notification1 = NotificationEntity.builder()
                .id(1L)
                .message("Task deadline approaching")
                .timestamp(LocalDateTime.now().minusHours(1))
                .relatedTaskId(1L)
                .recipientId(testRecipientId)
                .viewed(true)
                .build();

        notification2 = NotificationEntity.builder()
                .id(2L)
                .message("Task completed")
                .timestamp(LocalDateTime.now().minusMinutes(30))
                .relatedTaskId(2L)
                .recipientId(testRecipientId)
                .viewed(true)
                .build();

        unreadNotification = NotificationEntity.builder()
                .id(3L)
                .message("New task assigned")
                .timestamp(LocalDateTime.now().minusMinutes(10))
                .relatedTaskId(3L)
                .recipientId(testRecipientId)
                .viewed(false)
                .build();
    }

    @Test
    void testGetAllUserNotifications_Success() {
        // Given
        List<NotificationEntity> expectedNotifications = Arrays.asList(notification1, notification2, unreadNotification);
        when(notificationRepository.findAllByRecipientId(testRecipientId))
                .thenReturn(expectedNotifications);

        // When
        List<NotificationEntity> actualNotifications = notificationService.getAllUserNotifications(testRecipientId);

        // Then
        assertEquals(expectedNotifications, actualNotifications);
        verify(notificationRepository).findAllByRecipientId(testRecipientId);
    }

    @Test
    void testGetAllUserNotifications_EmptyResult() {
        // Given
        when(notificationRepository.findAllByRecipientId(testRecipientId))
                .thenReturn(Collections.emptyList());

        // When
        List<NotificationEntity> actualNotifications = notificationService.getAllUserNotifications(testRecipientId);

        // Then
        assertTrue(actualNotifications.isEmpty());
        verify(notificationRepository).findAllByRecipientId(testRecipientId);
    }

    @Test
    void testGetUnreadNotifications_Success() {
        // Given
        List<NotificationEntity> expectedNotifications = Collections.singletonList(unreadNotification);
        when(notificationRepository.findPendingNotificationsByRecipientId(testRecipientId))
                .thenReturn(expectedNotifications);

        // When
        List<NotificationEntity> actualNotifications = notificationService.getUnreadNotifications(testRecipientId);

        // Then
        assertEquals(expectedNotifications, actualNotifications);
        verify(notificationRepository).findPendingNotificationsByRecipientId(testRecipientId);
    }

    @Test
    void testGetUnreadNotifications_EmptyResult() {
        // Given
        when(notificationRepository.findPendingNotificationsByRecipientId(testRecipientId))
                .thenReturn(Collections.emptyList());

        // When
        List<NotificationEntity> actualNotifications = notificationService.getUnreadNotifications(testRecipientId);

        // Then
        assertTrue(actualNotifications.isEmpty());
        verify(notificationRepository).findPendingNotificationsByRecipientId(testRecipientId);
    }


    @Test
    void testGetAllUserNotifications_DifferentRecipients() {
        // Given
        Long recipientId2 = 2L;
        NotificationEntity notificationForUser2 = NotificationEntity.builder()
                .id(4L)
                .message("Task for user 2")
                .timestamp(LocalDateTime.now())
                .relatedTaskId(4L)
                .recipientId(recipientId2)
                .viewed(false)
                .build();

        when(notificationRepository.findAllByRecipientId(testRecipientId))
                .thenReturn(Arrays.asList(notification1, notification2, unreadNotification));
        when(notificationRepository.findAllByRecipientId(recipientId2))
                .thenReturn(Collections.singletonList(notificationForUser2));

        // When
        List<NotificationEntity> user1Notifications = notificationService.getAllUserNotifications(testRecipientId);
        List<NotificationEntity> user2Notifications = notificationService.getAllUserNotifications(recipientId2);

        // Then
        assertEquals(3, user1Notifications.size());
        assertEquals(1, user2Notifications.size());
        assertTrue(user1Notifications.contains(notification1));
        assertTrue(user1Notifications.contains(notification2));
        assertTrue(user1Notifications.contains(unreadNotification));
        assertTrue(user2Notifications.contains(notificationForUser2));
    }

    @Test
    void testGetUnreadNotifications_OnlyUnread() {
        // Given
        List<NotificationEntity> allNotifications = Arrays.asList(notification1, notification2, unreadNotification);
        List<NotificationEntity> unreadNotifications = Collections.singletonList(unreadNotification);

        when(notificationRepository.findAllByRecipientId(testRecipientId))
                .thenReturn(allNotifications);
        when(notificationRepository.findPendingNotificationsByRecipientId(testRecipientId))
                .thenReturn(unreadNotifications);

        // When
        List<NotificationEntity> allUserNotifications = notificationService.getAllUserNotifications(testRecipientId);
        List<NotificationEntity> unreadUserNotifications = notificationService.getUnreadNotifications(testRecipientId);

        // Then
        assertEquals(3, allUserNotifications.size());
        assertEquals(1, unreadUserNotifications.size());
        assertTrue(unreadUserNotifications.contains(unreadNotification));
        assertFalse(unreadUserNotifications.contains(notification1));
        assertFalse(unreadUserNotifications.contains(notification2));
    }

    @Test
    void testNotificationOrdering() {
        // Given
        NotificationEntity olderNotification = NotificationEntity.builder()
                .id(5L)
                .message("Older notification")
                .timestamp(LocalDateTime.now().minusHours(2))
                .relatedTaskId(5L)
                .recipientId(testRecipientId)
                .viewed(false)
                .build();

        NotificationEntity newerNotification = NotificationEntity.builder()
                .id(6L)
                .message("Newer notification")
                .timestamp(LocalDateTime.now().minusMinutes(5))
                .relatedTaskId(6L)
                .recipientId(testRecipientId)
                .viewed(false)
                .build();

        List<NotificationEntity> orderedNotifications = Arrays.asList(newerNotification, unreadNotification, olderNotification);
        when(notificationRepository.findAllByRecipientId(testRecipientId))
                .thenReturn(orderedNotifications);

        // When
        List<NotificationEntity> result = notificationService.getAllUserNotifications(testRecipientId);

        // Then
        assertEquals(3, result.size());
        assertEquals(newerNotification, result.get(0));
        assertEquals(unreadNotification, result.get(1));
        assertEquals(olderNotification, result.get(2));
    }

}
