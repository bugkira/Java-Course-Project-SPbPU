package spbstu.TasksApplication.repository.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spbstu.TasksApplication.model.Notification;
import spbstu.TasksApplication.repository.NotificationRepository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("h2")
public interface JpaNotificationRepository extends JpaRepository<Notification, Long>, NotificationRepository {
    List<Notification> findByUserIdAndIsReadFalse(Long userId);
    List<Notification> findByUserId(Long userId);
    Optional<Notification> findById(Long id);
}
