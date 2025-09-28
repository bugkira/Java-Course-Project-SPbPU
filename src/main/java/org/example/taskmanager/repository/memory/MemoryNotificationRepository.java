package org.example.taskmanager.repository.memory;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.example.taskmanager.domain.NotificationEntity;
import org.example.taskmanager.repository.NotificationDataRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
@Profile("inmemory")
public class MemoryNotificationRepository implements NotificationDataRepository {
    private final Map<Long, NotificationEntity> storage = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Optional<NotificationEntity> findById(Long notificationId) {
        return Optional.ofNullable(storage.get(notificationId));
    }

    @Override
    public List<NotificationEntity> findAllByRecipientOrderByTimestampDesc(Long recipientId) {
        return storage.values().stream()
                .filter(notification -> notification.getRecipientId().equals(recipientId))
                .sorted(Comparator.comparing(NotificationEntity::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationEntity> findUnviewedByRecipientOrderByTimestampDesc(Long recipientId) {
        return storage.values().stream()
                .filter(notification -> notification.getRecipientId().equals(recipientId) 
                    && !notification.getViewed())
                .sorted(Comparator.comparing(NotificationEntity::getTimestamp).reversed())
                .collect(Collectors.toList());
    }
}
