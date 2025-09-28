package org.example.taskmanager.repository.memory;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.example.taskmanager.domain.NotificationEntity;
import org.example.taskmanager.repository.NotificationDataRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("inmemory")
public class MemoryNotificationRepository implements NotificationDataRepository {
    private final Map<Long, NotificationEntity> storage = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public List<NotificationEntity> findAllByRecipientId(Long recipientId) {
        return storage.values().stream()
                .filter(notification -> notification.getRecipientId().equals(recipientId))
                .sorted(Comparator.comparing(NotificationEntity::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationEntity> findPendingNotificationsByRecipientId(Long recipientId) {
        return storage.values().stream()
                .filter(notification -> notification.getRecipientId().equals(recipientId) 
                    && !notification.getViewed())
                .sorted(Comparator.comparing(NotificationEntity::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationEntity> findByRelatedTaskId(Long relatedTaskId) {
        return storage.values().stream()
                .filter(notification -> notification.getRelatedTaskId().equals(relatedTaskId))
                .collect(Collectors.toList());
    }
}
