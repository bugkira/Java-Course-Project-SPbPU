package org.example.taskmanager.repository.jpa.impl;

import java.util.List;

import org.example.taskmanager.domain.NotificationEntity;
import org.example.taskmanager.repository.NotificationDataRepository;
import org.example.taskmanager.repository.jpa.NotificationJpaRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile({"docker", "postgresql"})
public class JpaNotificationRepositoryImpl implements NotificationDataRepository {

    private final NotificationJpaRepository jpaRepository;

    public JpaNotificationRepositoryImpl(NotificationJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<NotificationEntity> findAllByRecipientId(Long recipientId) {
        return jpaRepository.findByRecipientId(recipientId);
    }

    @Override
    public List<NotificationEntity> findPendingNotificationsByRecipientId(Long recipientId) {
        return jpaRepository.findPendingNotificationsByRecipientId(recipientId);
    }

    @Override
    public List<NotificationEntity> findByRelatedTaskId(Long relatedTaskId) {
        return jpaRepository.findByRelatedTaskId(relatedTaskId);
    }
}
