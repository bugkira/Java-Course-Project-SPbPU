package org.example.taskmanager.repository.jpa;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.example.taskmanager.domain.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskJpaRepository extends JpaRepository<TaskEntity, Long> {
    
    /**
     * Find all tasks by owner ID that are not removed
     */
    @Query("SELECT t FROM TaskEntity t WHERE t.ownerId = :ownerId AND t.removed = false")
    List<TaskEntity> findByOwnerIdAndNotRemoved(@Param("ownerId") Long ownerId);
    
    /**
     * Find all pending tasks by owner ID (not finished and not removed)
     */
    @Query("SELECT t FROM TaskEntity t WHERE t.ownerId = :ownerId AND t.finished = false AND t.removed = false")
    List<TaskEntity> findPendingTasksByOwnerId(@Param("ownerId") Long ownerId);
    
    /**
     * Find task by ID and owner ID (for security)
     */
    @Query("SELECT t FROM TaskEntity t WHERE t.id = :id AND t.ownerId = :ownerId")
    Optional<TaskEntity> findByIdAndOwnerId(@Param("id") Long id, @Param("ownerId") Long ownerId);
    
    /**
     * Find all overdue tasks (due date is before current time, not finished, not removed)
     */
    @Query("SELECT t FROM TaskEntity t WHERE t.dueDate < :currentTime AND t.finished = false AND t.removed = false")
    List<TaskEntity> findOverdueTasks(@Param("currentTime") LocalDateTime currentTime);
}
