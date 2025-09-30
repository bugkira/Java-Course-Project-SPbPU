package org.example.taskmanager.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.example.taskmanager.domain.TaskEntity;

public interface TaskDataRepository {
    List<TaskEntity> findAllByOwnerId(Long ownerId);
    List<TaskEntity> findPendingTasksByOwnerId(Long ownerId);
    List<TaskEntity> findOverdueTasks(LocalDateTime currentTime);
    Optional<TaskEntity> findByIdAndOwnerId(Long id, Long ownerId);
    TaskEntity save(TaskEntity task);
    void deleteById(Long id);
}
