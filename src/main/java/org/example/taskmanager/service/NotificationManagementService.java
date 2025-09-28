package org.example.taskmanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.example.taskmanager.exception.EntityNotFoundException;
import org.example.taskmanager.domain.NotificationEntity;
import org.example.taskmanager.repository.NotificationDataRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationManagementService {

    private final NotificationDataRepository notificationRepository;

    public List<NotificationEntity> getAllUserNotifications(Long recipientId) {
        return notificationRepository.findAllByRecipientId(recipientId);
    }

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
