package org.example.taskmanager.repository.memory;

import java.util.List;
import java.util.Optional;

import org.example.taskmanager.domain.NotificationEntity;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("inmemory")
class MemoryNotificationRepositoryTest {

    @Test
    void testFindAllByRecipientOrderByTimestampDesc_EmptyResult() {
        // Given
        MemoryNotificationRepository repository = new MemoryNotificationRepository();
        Long recipientId = 1L;

        // When
        List<NotificationEntity> result = repository.findAllByRecipientOrderByTimestampDesc(recipientId);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindUnviewedByRecipientOrderByTimestampDesc_EmptyResult() {
        // Given
        MemoryNotificationRepository repository = new MemoryNotificationRepository();
        Long recipientId = 1L;

        // When
        List<NotificationEntity> result = repository.findUnviewedByRecipientOrderByTimestampDesc(recipientId);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindById_NotFound() {
        // Given
        MemoryNotificationRepository repository = new MemoryNotificationRepository();

        // When
        Optional<NotificationEntity> result = repository.findById(999L);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testRepositoryInitialization() {
        // When
        MemoryNotificationRepository repository = new MemoryNotificationRepository();

        // Then
        assertNotNull(repository);
    }

    @Test
    void testRepositoryProfile() {
        // Given
        MemoryNotificationRepository repository = new MemoryNotificationRepository();

        // When & Then
        // Test that repository can be instantiated without errors
        assertNotNull(repository);
        
        // Test that methods can be called without errors
        List<NotificationEntity> allNotifications = repository.findAllByRecipientOrderByTimestampDesc(1L);
        List<NotificationEntity> unreadNotifications = repository.findUnviewedByRecipientOrderByTimestampDesc(1L);
        Optional<NotificationEntity> notification = repository.findById(1L);
        
        assertNotNull(allNotifications);
        assertNotNull(unreadNotifications);
        assertNotNull(notification);
    }

    @Test
    void testRepositoryMethodsReturnCorrectTypes() {
        // Given
        MemoryNotificationRepository repository = new MemoryNotificationRepository();

        // When
        List<NotificationEntity> allNotifications = repository.findAllByRecipientOrderByTimestampDesc(1L);
        List<NotificationEntity> unreadNotifications = repository.findUnviewedByRecipientOrderByTimestampDesc(1L);
        Optional<NotificationEntity> notification = repository.findById(1L);

        // Then
        assertTrue(allNotifications instanceof List);
        assertTrue(unreadNotifications instanceof List);
        assertTrue(notification instanceof Optional);
    }

    @Test
    void testRepositoryHandlesNullRecipientId() {
        // Given
        MemoryNotificationRepository repository = new MemoryNotificationRepository();

        // When & Then
        // Should not throw exception, but return empty results
        assertDoesNotThrow(() -> {
            List<NotificationEntity> result = repository.findAllByRecipientOrderByTimestampDesc(null);
            assertTrue(result.isEmpty());
        });

        assertDoesNotThrow(() -> {
            List<NotificationEntity> result = repository.findUnviewedByRecipientOrderByTimestampDesc(null);
            assertTrue(result.isEmpty());
        });
    }

    @Test
    void testRepositoryHandlesNullNotificationId() {
        // Given
        MemoryNotificationRepository repository = new MemoryNotificationRepository();

        // When
        Optional<NotificationEntity> result = repository.findById(null);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testRepositoryHandlesNegativeIds() {
        // Given
        MemoryNotificationRepository repository = new MemoryNotificationRepository();

        // When & Then
        assertDoesNotThrow(() -> {
            List<NotificationEntity> result = repository.findAllByRecipientOrderByTimestampDesc(-1L);
            assertTrue(result.isEmpty());
        });

        assertDoesNotThrow(() -> {
            List<NotificationEntity> result = repository.findUnviewedByRecipientOrderByTimestampDesc(-1L);
            assertTrue(result.isEmpty());
        });

        assertDoesNotThrow(() -> {
            Optional<NotificationEntity> result = repository.findById(-1L);
            assertFalse(result.isPresent());
        });
    }

    @Test
    void testRepositoryHandlesZeroIds() {
        // Given
        MemoryNotificationRepository repository = new MemoryNotificationRepository();

        // When & Then
        assertDoesNotThrow(() -> {
            List<NotificationEntity> result = repository.findAllByRecipientOrderByTimestampDesc(0L);
            assertTrue(result.isEmpty());
        });

        assertDoesNotThrow(() -> {
            List<NotificationEntity> result = repository.findUnviewedByRecipientOrderByTimestampDesc(0L);
            assertTrue(result.isEmpty());
        });

        assertDoesNotThrow(() -> {
            Optional<NotificationEntity> result = repository.findById(0L);
            assertFalse(result.isPresent());
        });
    }

    @Test
    void testRepositoryHandlesLargeIds() {
        // Given
        MemoryNotificationRepository repository = new MemoryNotificationRepository();
        Long largeId = Long.MAX_VALUE;

        // When & Then
        assertDoesNotThrow(() -> {
            List<NotificationEntity> result = repository.findAllByRecipientOrderByTimestampDesc(largeId);
            assertTrue(result.isEmpty());
        });

        assertDoesNotThrow(() -> {
            List<NotificationEntity> result = repository.findUnviewedByRecipientOrderByTimestampDesc(largeId);
            assertTrue(result.isEmpty());
        });

        assertDoesNotThrow(() -> {
            Optional<NotificationEntity> result = repository.findById(largeId);
            assertFalse(result.isPresent());
        });
    }
}