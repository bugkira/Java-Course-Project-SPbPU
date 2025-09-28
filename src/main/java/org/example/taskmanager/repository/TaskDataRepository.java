package org.example.taskmanager.repository;

import java.util.List;
import java.util.Optional;

import org.example.taskmanager.domain.TaskEntity;

public interface TaskDataRepository {
    List<TaskEntity> findAllByOwnerAndNotRemoved(Long ownerId);
    List<TaskEntity> findPendingByOwnerAndNotRemoved(Long ownerId);
    Optional<TaskEntity> findByIdAndNotRemoved(Long id);
    TaskEntity store(TaskEntity task);
}
