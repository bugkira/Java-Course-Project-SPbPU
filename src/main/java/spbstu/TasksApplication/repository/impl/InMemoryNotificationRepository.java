package spbstu.TasksApplication.repository.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import spbstu.TasksApplication.model.Notification;
import spbstu.TasksApplication.repository.NotificationRepository;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Profile("inmemory")
public class InMemoryNotificationRepository implements NotificationRepository {
    private final Map<Long, Notification> notifications = new HashMap<>();
    private final AtomicLong notificationIdCounter = new AtomicLong(1);

    @Override
    public List<Notification> findByUserIdAndIsReadFalse(Long userId) {
        List<Notification> result = new ArrayList<>();
        for (Notification notification : notifications.values()) {
            if (notification.getUserId().equals(userId) && !notification.getIsRead()) {
                result.add(notification);
            }
        }
        return result;
    }

    @Override
    public List<Notification> findByUserId(Long userId) {
        List<Notification> result = new ArrayList<>();
        for (Notification notification : notifications.values()) {
            if (notification.getUserId().equals(userId)) {
                result.add(notification);
            }
        }
        return result;
    }

    @Override
    public Optional<Notification> findById(Long id) {
        return Optional.ofNullable(notifications.get(id));
    }

    @Override
    public Notification save(Notification notification) {
        if (notification.getNotificationId() == null) {
            notification.setNotificationId(notificationIdCounter.getAndIncrement());
        }
        notifications.put(notification.getNotificationId(), notification);
        return notification;
    }

    @Override
    public void deleteById(Long id) {
        notifications.remove(id);
    }
}
