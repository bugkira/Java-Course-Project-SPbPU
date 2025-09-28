package org.example.taskmanager.repository.jpa;

import java.util.List;

import org.example.taskmanager.domain.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationJpaRepository extends JpaRepository<NotificationEntity, Long> {
    
    /**
     * Find all notifications by recipient ID
     */
    List<NotificationEntity> findByRecipientId(Long recipientId);
    
    /**
     * Find all pending notifications by recipient ID (not viewed)
     */
    @Query("SELECT n FROM NotificationEntity n WHERE n.recipientId = :recipientId AND n.viewed = false")
    List<NotificationEntity> findPendingNotificationsByRecipientId(@Param("recipientId") Long recipientId);
    
    /**
     * Find notifications by related task ID
     */
    List<NotificationEntity> findByRelatedTaskId(Long relatedTaskId);
}
