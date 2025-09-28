package org.example.taskmanager.repository;

import java.util.List;
import java.util.Optional;

import org.example.taskmanager.domain.NotificationEntity;

public interface NotificationDataRepository {
    Optional<NotificationEntity> findById(Long notificationId);
    List<NotificationEntity> findAllByRecipientOrderByTimestampDesc(Long recipientId);
    List<NotificationEntity> findUnviewedByRecipientOrderByTimestampDesc(Long recipientId);
}
