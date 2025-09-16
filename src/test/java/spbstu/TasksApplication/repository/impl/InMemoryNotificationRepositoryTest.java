package spbstu.TasksApplication.repository.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spbstu.TasksApplication.model.Notification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryNotificationRepositoryTest {
    private InMemoryNotificationRepository notificationRepository;
    private Notification testNotification;

    @BeforeEach
    void setUp() {
        notificationRepository = new InMemoryNotificationRepository();
        testNotification = Notification.builder()
                .text("Test notification")
                .date(LocalDateTime.now())
                .taskId(1L)
                .userId(1L)
                .isRead(false)
                .build();
    }

    @Test
    void save_ShouldCreateNewNotification_WhenNotificationIdIsNull() {
        Notification savedNotification = notificationRepository.save(testNotification);
        
        assertNotNull(savedNotification.getNotificationId());
        assertEquals(testNotification.getText(), savedNotification.getText());
        assertEquals(testNotification.getUserId(), savedNotification.getUserId());
    }

    @Test
    void save_ShouldUpdateExistingNotification_WhenNotificationIdIsNotNull() {
        Notification savedNotification = notificationRepository.save(testNotification);
        savedNotification.setText("Updated notification");
        
        Notification updatedNotification = notificationRepository.save(savedNotification);
        
        assertEquals(savedNotification.getNotificationId(), updatedNotification.getNotificationId());
        assertEquals("Updated notification", updatedNotification.getText());
    }

    @Test
    void findByUserId_ShouldReturnAllNotificationsForUser() {
        notificationRepository.save(testNotification);
        
        Notification anotherNotification = Notification.builder()
                .text("Another notification")
                .date(LocalDateTime.now())
                .taskId(2L)
                .userId(1L)
                .isRead(true)
                .build();
        notificationRepository.save(anotherNotification);
        
        Notification otherUserNotification = Notification.builder()
                .text("Other user notification")
                .date(LocalDateTime.now())
                .taskId(3L)
                .userId(2L)
                .isRead(false)
                .build();
        notificationRepository.save(otherUserNotification);
        
        List<Notification> notifications = notificationRepository.findByUserId(1L);
        
        assertEquals(2, notifications.size());
    }

    @Test
    void findByUserIdAndIsReadFalse_ShouldReturnOnlyUnreadNotifications() {
        notificationRepository.save(testNotification);
        
        Notification readNotification = Notification.builder()
                .text("Read notification")
                .date(LocalDateTime.now())
                .taskId(2L)
                .userId(1L)
                .isRead(true)
                .build();
        notificationRepository.save(readNotification);
        
        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndIsReadFalse(1L);
        
        assertEquals(1, unreadNotifications.size());
        assertEquals(testNotification.getText(), unreadNotifications.get(0).getText());
    }

    @Test
    void findById_ShouldReturnNotification_WhenExists() {
        Notification savedNotification = notificationRepository.save(testNotification);
        Optional<Notification> foundNotification = notificationRepository.findById(savedNotification.getNotificationId());
        
        assertTrue(foundNotification.isPresent());
        assertEquals(savedNotification.getNotificationId(), foundNotification.get().getNotificationId());
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotExists() {
        Optional<Notification> foundNotification = notificationRepository.findById(999L);
        
        assertTrue(foundNotification.isEmpty());
    }

    @Test
    void deleteById_ShouldDeleteNotification() {
        Notification savedNotification = notificationRepository.save(testNotification);
        notificationRepository.deleteById(savedNotification.getNotificationId());
        
        Optional<Notification> foundNotification = notificationRepository.findById(savedNotification.getNotificationId());
        assertTrue(foundNotification.isEmpty());
    }

    @Test
    void deleteById_ShouldNotAffectOtherNotifications() {
        Notification notification1 = notificationRepository.save(testNotification);
        
        Notification notification2 = Notification.builder()
                .text("Another notification")
                .date(LocalDateTime.now())
                .taskId(2L)
                .userId(1L)
                .isRead(false)
                .build();
        notificationRepository.save(notification2);
        
        notificationRepository.deleteById(notification1.getNotificationId());
        
        Optional<Notification> foundNotification1 = notificationRepository.findById(notification1.getNotificationId());
        Optional<Notification> foundNotification2 = notificationRepository.findById(notification2.getNotificationId());
        
        assertTrue(foundNotification1.isEmpty());
        assertTrue(foundNotification2.isPresent());
    }
}
