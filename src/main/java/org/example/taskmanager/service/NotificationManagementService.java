package org.example.taskmanager.service;

import java.util.List;

import org.example.taskmanager.domain.NotificationEntity;
import org.example.taskmanager.repository.NotificationDataRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationManagementService {

    private final NotificationDataRepository notificationRepository;

    @Cacheable(value = "notifications", key = "'all-notifications-' + #recipientId")
    public List<NotificationEntity> getAllUserNotifications(Long recipientId) {
        return notificationRepository.findAllByRecipientId(recipientId);
    }

    @Cacheable(value = "notifications", key = "'unread-notifications-' + #recipientId")
    public List<NotificationEntity> getUnreadNotifications(Long recipientId) {
        return notificationRepository.findPendingNotificationsByRecipientId(recipientId);
    }

    private void performNotificationValidation(NotificationEntity notification) {
        if (notification.getMessage() == null || notification.getMessage().trim().isEmpty()) {
            throw new IllegalArgumentException("Notification message must not be empty");
        }
        if (notification.getRelatedTaskId() == null) {
            throw new IllegalArgumentException("Related task ID must not be null");
        }
        if (notification.getRecipientId() == null) {
            throw new IllegalArgumentException("Recipient ID must not be null");
        }
    }
}
