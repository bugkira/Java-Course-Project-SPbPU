package spbstu.TasksApplication.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spbstu.TasksApplication.exception.ResourceNotFoundException;
import spbstu.TasksApplication.model.Notification;
import spbstu.TasksApplication.repository.impl.InMemoryNotificationRepository;
import spbstu.TasksApplication.service.NotificationService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationServiceTest {
    private NotificationService notificationService;
    private Notification testNotification;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(new InMemoryNotificationRepository());
        testNotification = Notification.builder()
                .text("Test notification")
                .date(LocalDateTime.now())
                .taskId(1L)
                .userId(1L)
                .isRead(false)
                .build();
    }

    @Test
    void createNotification_ShouldCreateNewNotification() {
        Notification createdNotification = notificationService.createNotification(testNotification);
        
        assertNotNull(createdNotification.getNotificationId());
        assertEquals(testNotification.getText(), createdNotification.getText());
        assertEquals(testNotification.getTaskId(), createdNotification.getTaskId());
        assertEquals(testNotification.getUserId(), createdNotification.getUserId());
    }

    @Test
    void getAllNotifications_ShouldReturnAllNotificationsForUser() {
        notificationService.createNotification(testNotification);
        
        Notification anotherNotification = Notification.builder()
                .text("Another notification")
                .date(LocalDateTime.now())
                .taskId(2L)
                .userId(1L)
                .isRead(true)
                .build();
        notificationService.createNotification(anotherNotification);
        
        List<Notification> notifications = notificationService.getAllNotifications(1L);
        
        assertEquals(2, notifications.size());
    }

    @Test
    void getUnreadNotifications_ShouldReturnOnlyUnreadNotifications() {
        notificationService.createNotification(testNotification);
        
        Notification readNotification = Notification.builder()
                .text("Read notification")
                .date(LocalDateTime.now())
                .taskId(2L)
                .userId(1L)
                .isRead(true)
                .build();
        notificationService.createNotification(readNotification);
        
        List<Notification> unreadNotifications = notificationService.getUnreadNotifications(1L);
        
        assertEquals(1, unreadNotifications.size());
        assertEquals(testNotification.getText(), unreadNotifications.get(0).getText());
    }

    @Test
    void getNotificationById_ShouldReturnNotification_WhenExists() {
        Notification createdNotification = notificationService.createNotification(testNotification);
        Notification foundNotification = notificationService.getNotificationById(createdNotification.getNotificationId());
        
        assertEquals(createdNotification.getNotificationId(), foundNotification.getNotificationId());
    }

    @Test
    void getNotificationById_ShouldThrowException_WhenNotExists() {
        assertThrows(ResourceNotFoundException.class, () -> notificationService.getNotificationById(999L));
    }

    @Test
    void markAsRead_ShouldMarkNotificationAsRead() {
        Notification createdNotification = notificationService.createNotification(testNotification);
        notificationService.markAsRead(createdNotification.getNotificationId());
        
        Notification readNotification = notificationService.getNotificationById(createdNotification.getNotificationId());
        assertTrue(readNotification.getIsRead());
    }

    @Test
    void deleteNotification_ShouldDeleteNotification() {
        Notification createdNotification = notificationService.createNotification(testNotification);
        notificationService.deleteNotification(createdNotification.getNotificationId());
        
        assertThrows(ResourceNotFoundException.class, () -> notificationService.getNotificationById(createdNotification.getNotificationId()));
    }
}
