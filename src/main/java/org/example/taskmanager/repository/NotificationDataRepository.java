package org.example.taskmanager.repository;

import java.util.List;

import org.example.taskmanager.domain.NotificationEntity;

public interface NotificationDataRepository {
    NotificationEntity save(NotificationEntity notification);
    List<NotificationEntity> findAllByRecipientId(Long recipientId);
    List<NotificationEntity> findPendingNotificationsByRecipientId(Long recipientId);
    List<NotificationEntity> findByRelatedTaskId(Long relatedTaskId);
}
