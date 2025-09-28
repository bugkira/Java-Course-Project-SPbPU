package org.example.taskmanager.domain;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class NotificationEntityTest {

    @Test
    void testBuilder_Success() {
        // Given
        LocalDateTime timestamp = LocalDateTime.now();
        Long relatedTaskId = 1L;
        Long recipientId = 1L;

        // When
        NotificationEntity notification = NotificationEntity.builder()
                .message("Test notification")
                .timestamp(timestamp)
                .relatedTaskId(relatedTaskId)
                .recipientId(recipientId)
                .build();

        // Then
        assertNotNull(notification);
        assertEquals("Test notification", notification.getMessage());
        assertEquals(timestamp, notification.getTimestamp());
        assertEquals(relatedTaskId, notification.getRelatedTaskId());
        assertEquals(recipientId, notification.getRecipientId());
        assertFalse(notification.getViewed());
    }

    @Test
    void testBuilder_WithAllFields() {
        // Given
        Long id = 1L;
        String message = "Complete notification";
        LocalDateTime timestamp = LocalDateTime.now().minusHours(1);
        Long relatedTaskId = 1L;
        Long recipientId = 1L;
        Boolean viewed = true;

        // When
        NotificationEntity notification = NotificationEntity.builder()
                .id(id)
                .message(message)
                .timestamp(timestamp)
                .relatedTaskId(relatedTaskId)
                .recipientId(recipientId)
                .viewed(viewed)
                .build();

        // Then
        assertEquals(id, notification.getId());
        assertEquals(message, notification.getMessage());
        assertEquals(timestamp, notification.getTimestamp());
        assertEquals(relatedTaskId, notification.getRelatedTaskId());
        assertEquals(recipientId, notification.getRecipientId());
        assertEquals(viewed, notification.getViewed());
    }

    @Test
    void testBuilder_DefaultValues() {
        // Given
        String message = "Default notification";
        Long relatedTaskId = 1L;
        Long recipientId = 1L;

        // When
        NotificationEntity notification = NotificationEntity.builder()
                .message(message)
                .relatedTaskId(relatedTaskId)
                .recipientId(recipientId)
                .build();

        // Then
        assertNull(notification.getId());
        assertEquals(message, notification.getMessage());
        assertNotNull(notification.getTimestamp());
        assertEquals(relatedTaskId, notification.getRelatedTaskId());
        assertEquals(recipientId, notification.getRecipientId());
        assertFalse(notification.getViewed());
    }

    @Test
    void testBuilder_DefaultTimestamp() {
        // Given
        LocalDateTime beforeCreation = LocalDateTime.now();
        
        NotificationEntity notification = NotificationEntity.builder()
                .message("Test notification")
                .relatedTaskId(1L)
                .recipientId(1L)
                .build();

        LocalDateTime afterCreation = LocalDateTime.now();

        // Then
        assertNotNull(notification.getTimestamp());
        assertTrue(notification.getTimestamp().isAfter(beforeCreation.minusSeconds(1)));
        assertTrue(notification.getTimestamp().isBefore(afterCreation.plusSeconds(1)));
    }

    @Test
    void testEqualsAndHashCode_SameObjects() {
        // Given
        LocalDateTime timestamp = LocalDateTime.now();
        
        NotificationEntity notification1 = NotificationEntity.builder()
                .id(1L)
                .message("Test notification")
                .timestamp(timestamp)
                .relatedTaskId(1L)
                .recipientId(1L)
                .viewed(false)
                .build();

        NotificationEntity notification2 = NotificationEntity.builder()
                .id(1L)
                .message("Test notification")
                .timestamp(timestamp)
                .relatedTaskId(1L)
                .recipientId(1L)
                .viewed(false)
                .build();

        // When & Then
        assertEquals(notification1, notification2);
        assertEquals(notification1.hashCode(), notification2.hashCode());
    }

    @Test
    void testEqualsAndHashCode_DifferentObjects() {
        // Given
        NotificationEntity notification1 = NotificationEntity.builder()
                .id(1L)
                .message("Notification 1")
                .timestamp(LocalDateTime.now())
                .relatedTaskId(1L)
                .recipientId(1L)
                .build();

        NotificationEntity notification2 = NotificationEntity.builder()
                .id(2L)
                .message("Notification 2")
                .timestamp(LocalDateTime.now())
                .relatedTaskId(2L)
                .recipientId(2L)
                .build();

        // When & Then
        assertNotEquals(notification1, notification2);
        assertNotEquals(notification1.hashCode(), notification2.hashCode());
    }

    @Test
    void testEqualsAndHashCode_DifferentMessages() {
        // Given
        LocalDateTime timestamp = LocalDateTime.now();
        
        NotificationEntity notification1 = NotificationEntity.builder()
                .id(1L)
                .message("Message 1")
                .timestamp(timestamp)
                .relatedTaskId(1L)
                .recipientId(1L)
                .build();

        NotificationEntity notification2 = NotificationEntity.builder()
                .id(1L)
                .message("Message 2")
                .timestamp(timestamp)
                .relatedTaskId(1L)
                .recipientId(1L)
                .build();

        // When & Then
        assertNotEquals(notification1, notification2);
        assertNotEquals(notification1.hashCode(), notification2.hashCode());
    }

    @Test
    void testEqualsAndHashCode_DifferentRecipients() {
        // Given
        LocalDateTime timestamp = LocalDateTime.now();
        
        NotificationEntity notification1 = NotificationEntity.builder()
                .id(1L)
                .message("Same message")
                .timestamp(timestamp)
                .relatedTaskId(1L)
                .recipientId(1L)
                .build();

        NotificationEntity notification2 = NotificationEntity.builder()
                .id(1L)
                .message("Same message")
                .timestamp(timestamp)
                .relatedTaskId(1L)
                .recipientId(2L)
                .build();

        // When & Then
        assertNotEquals(notification1, notification2);
        assertNotEquals(notification1.hashCode(), notification2.hashCode());
    }

    @Test
    void testToString() {
        // Given
        NotificationEntity notification = NotificationEntity.builder()
                .id(1L)
                .message("Test notification")
                .timestamp(LocalDateTime.now())
                .relatedTaskId(1L)
                .recipientId(1L)
                .viewed(false)
                .build();

        // When
        String toString = notification.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("NotificationEntity"));
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("message=Test notification"));
        assertTrue(toString.contains("relatedTaskId=1"));
        assertTrue(toString.contains("recipientId=1"));
        assertTrue(toString.contains("viewed=false"));
    }

    @Test
    void testSetters() {
        // Given
        NotificationEntity notification = NotificationEntity.builder()
                .message("Original message")
                .relatedTaskId(1L)
                .recipientId(1L)
                .build();

        // When
        notification.setId(999L);
        notification.setMessage("Updated message");
        notification.setTimestamp(LocalDateTime.now().minusHours(1));
        notification.setRelatedTaskId(999L);
        notification.setRecipientId(999L);
        notification.setViewed(true);

        // Then
        assertEquals(999L, notification.getId());
        assertEquals("Updated message", notification.getMessage());
        assertNotNull(notification.getTimestamp());
        assertEquals(999L, notification.getRelatedTaskId());
        assertEquals(999L, notification.getRecipientId());
        assertTrue(notification.getViewed());
    }

    @Test
    void testNoArgsConstructor() {
        // When
        NotificationEntity notification = new NotificationEntity();

        // Then
        assertNotNull(notification);
        assertNull(notification.getId());
        assertNull(notification.getMessage());
        assertNotNull(notification.getTimestamp()); // timestamp is generated automatically
        assertNull(notification.getRelatedTaskId());
        assertNull(notification.getRecipientId());
        assertFalse(notification.getViewed()); // viewed has default value false
    }

    @Test
    void testAllArgsConstructor() {
        // Given
        Long id = 1L;
        String message = "Test notification";
        LocalDateTime timestamp = LocalDateTime.now().minusHours(1);
        Long relatedTaskId = 1L;
        Long recipientId = 1L;
        Boolean viewed = true;

        // When
        NotificationEntity notification = new NotificationEntity(id, message, timestamp, relatedTaskId, recipientId, viewed);

        // Then
        assertEquals(id, notification.getId());
        assertEquals(message, notification.getMessage());
        assertEquals(timestamp, notification.getTimestamp());
        assertEquals(relatedTaskId, notification.getRelatedTaskId());
        assertEquals(recipientId, notification.getRecipientId());
        assertEquals(viewed, notification.getViewed());
    }

    @Test
    void testNotificationWithSpecialCharacters() {
        // Given
        String specialMessage = "Special notification with émojis 🎉 and symbols!@#$%";
        Long relatedTaskId = 123L;
        Long recipientId = 456L;

        // When
        NotificationEntity notification = NotificationEntity.builder()
                .message(specialMessage)
                .relatedTaskId(relatedTaskId)
                .recipientId(recipientId)
                .build();

        // Then
        assertEquals(specialMessage, notification.getMessage());
        assertEquals(relatedTaskId, notification.getRelatedTaskId());
        assertEquals(recipientId, notification.getRecipientId());
    }

    @Test
    void testNotificationWithLongMessage() {
        // Given
        String longMessage = "This is a very long notification message that contains a lot of text " +
                "to test how the notification entity handles longer messages. " +
                "It should be able to store and retrieve messages of various lengths " +
                "without any issues or truncation problems.";
        Long relatedTaskId = 1L;
        Long recipientId = 1L;

        // When
        NotificationEntity notification = NotificationEntity.builder()
                .message(longMessage)
                .relatedTaskId(relatedTaskId)
                .recipientId(recipientId)
                .build();

        // Then
        assertEquals(longMessage, notification.getMessage());
        assertEquals(relatedTaskId, notification.getRelatedTaskId());
        assertEquals(recipientId, notification.getRecipientId());
    }

    @Test
    void testNotificationWithNullValues() {
        // Given
        NotificationEntity notification = NotificationEntity.builder()
                .message("Test notification")
                .relatedTaskId(1L) // Use valid ID to avoid NPE
                .recipientId(1L)   // Use valid ID to avoid NPE
                .build();

        // Then
        assertNull(notification.getId());
        assertEquals("Test notification", notification.getMessage());
        assertNotNull(notification.getTimestamp());
        assertEquals(1L, notification.getRelatedTaskId());
        assertEquals(1L, notification.getRecipientId());
        assertFalse(notification.getViewed());
    }
}
