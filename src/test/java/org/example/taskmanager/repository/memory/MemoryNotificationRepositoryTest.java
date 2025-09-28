package org.example.taskmanager.repository.memory;

import java.time.LocalDateTime;
import java.util.List;

import org.example.taskmanager.domain.NotificationEntity;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("inmemory")
class MemoryNotificationRepositoryTest {

    private MemoryNotificationRepository repository;
    private NotificationEntity notification1;
    private NotificationEntity notification2;
    private NotificationEntity notification3;

    @BeforeEach
    void setUp() {
        repository = new MemoryNotificationRepository();
        
        notification1 = NotificationEntity.builder()
                .id(1L)
                .message("Test notification 1")
                .timestamp(LocalDateTime.now())
                .relatedTaskId(1L)
                .recipientId(1L)
                .viewed(false)
                .build();
                
        notification2 = NotificationEntity.builder()
                .id(2L)
                .message("Test notification 2")
                .timestamp(LocalDateTime.now())
                .relatedTaskId(2L)
                .recipientId(1L)
                .viewed(true)
                .build();
                
        notification3 = NotificationEntity.builder()
                .id(3L)
                .message("Test notification 3")
                .timestamp(LocalDateTime.now())
                .relatedTaskId(3L)
                .recipientId(2L)
                .viewed(false)
                .build();
    }

    @Test
    void testFindAllByRecipientId_Success() {
        // Given
        Long recipientId = 1L;
        
        // When
        List<NotificationEntity> result = repository.findAllByRecipientId(recipientId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindPendingNotificationsByRecipientId_Success() {
        // Given
        Long recipientId = 1L;
        
        // When
        List<NotificationEntity> result = repository.findPendingNotificationsByRecipientId(recipientId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByRelatedTaskId_Success() {
        // Given
        Long relatedTaskId = 1L;
        
        // When
        List<NotificationEntity> result = repository.findByRelatedTaskId(relatedTaskId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testRepositoryInitialization() {
        // When
        MemoryNotificationRepository repository = new MemoryNotificationRepository();

        // Then
        assertNotNull(repository);
    }

    @Test
    void testFindAllByRecipientId_EmptyResult() {
        // Given
        Long nonExistentRecipientId = 999L;

        // When
        List<NotificationEntity> result = repository.findAllByRecipientId(nonExistentRecipientId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindPendingNotificationsByRecipientId_EmptyResult() {
        // Given
        Long nonExistentRecipientId = 999L;

        // When
        List<NotificationEntity> result = repository.findPendingNotificationsByRecipientId(nonExistentRecipientId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByRelatedTaskId_EmptyResult() {
        // Given
        Long nonExistentTaskId = 999L;

        // When
        List<NotificationEntity> result = repository.findByRelatedTaskId(nonExistentTaskId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindAllByRecipientId_NullInput() {
        // When & Then
        assertDoesNotThrow(() -> {
            List<NotificationEntity> result = repository.findAllByRecipientId(null);
            assertNotNull(result);
        });
    }

    @Test
    void testFindPendingNotificationsByRecipientId_NullInput() {
        // When & Then
        assertDoesNotThrow(() -> {
            List<NotificationEntity> result = repository.findPendingNotificationsByRecipientId(null);
            assertNotNull(result);
        });
    }

    @Test
    void testFindByRelatedTaskId_NullInput() {
        // When & Then
        assertDoesNotThrow(() -> {
            List<NotificationEntity> result = repository.findByRelatedTaskId(null);
            assertNotNull(result);
        });
    }

    @Test
    void testFindAllByRecipientId_NegativeId() {
        // When & Then
        assertDoesNotThrow(() -> {
            List<NotificationEntity> result = repository.findAllByRecipientId(-1L);
            assertNotNull(result);
        });
    }

    @Test
    void testFindPendingNotificationsByRecipientId_NegativeId() {
        // When & Then
        assertDoesNotThrow(() -> {
            List<NotificationEntity> result = repository.findPendingNotificationsByRecipientId(-1L);
            assertNotNull(result);
        });
    }

    @Test
    void testFindByRelatedTaskId_NegativeId() {
        // When & Then
        assertDoesNotThrow(() -> {
            List<NotificationEntity> result = repository.findByRelatedTaskId(-1L);
            assertNotNull(result);
        });
    }

    @Test
    void testFindAllByRecipientId_ZeroId() {
        // When & Then
        assertDoesNotThrow(() -> {
            List<NotificationEntity> result = repository.findAllByRecipientId(0L);
            assertNotNull(result);
        });
    }

    @Test
    void testFindPendingNotificationsByRecipientId_ZeroId() {
        // When & Then
        assertDoesNotThrow(() -> {
            List<NotificationEntity> result = repository.findPendingNotificationsByRecipientId(0L);
            assertNotNull(result);
        });
    }

    @Test
    void testFindByRelatedTaskId_ZeroId() {
        // When & Then
        assertDoesNotThrow(() -> {
            List<NotificationEntity> result = repository.findByRelatedTaskId(0L);
            assertNotNull(result);
        });
    }

    @Test
    void testFindAllByRecipientId_LargeId() {
        // Given
        Long largeId = Long.MAX_VALUE;

        // When & Then
        assertDoesNotThrow(() -> {
            List<NotificationEntity> result = repository.findAllByRecipientId(largeId);
            assertNotNull(result);
        });
    }

    @Test
    void testFindPendingNotificationsByRecipientId_LargeId() {
        // Given
        Long largeId = Long.MAX_VALUE;

        // When & Then
        assertDoesNotThrow(() -> {
            List<NotificationEntity> result = repository.findPendingNotificationsByRecipientId(largeId);
            assertNotNull(result);
        });
    }

    @Test
    void testFindByRelatedTaskId_LargeId() {
        // Given
        Long largeId = Long.MAX_VALUE;

        // When & Then
        assertDoesNotThrow(() -> {
            List<NotificationEntity> result = repository.findByRelatedTaskId(largeId);
            assertNotNull(result);
        });
    }
}
