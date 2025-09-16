package spbstu.TasksApplication.repository;

import spbstu.TasksApplication.model.Notification;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository {
    List<Notification> findByUserIdAndIsReadFalse(Long userId);
    List<Notification> findByUserId(Long userId);
    Optional<Notification> findById(Long id);
    Notification save(Notification notification);
    void deleteById(Long id);
}
