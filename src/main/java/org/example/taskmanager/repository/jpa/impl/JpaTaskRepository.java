package org.example.taskmanager.repository.jpa.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.example.taskmanager.domain.TaskEntity;
import org.example.taskmanager.repository.TaskDataRepository;
import org.example.taskmanager.repository.jpa.TaskJpaRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("h2")
public class JpaTaskRepository implements TaskDataRepository {
    
    private final TaskJpaRepository jpaRepository;
    
    public JpaTaskRepository(TaskJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public List<TaskEntity> findAllByOwnerId(Long ownerId) {
        return jpaRepository.findByOwnerIdAndNotRemoved(ownerId);
    }
    
    @Override
    public List<TaskEntity> findPendingTasksByOwnerId(Long ownerId) {
        return jpaRepository.findPendingTasksByOwnerId(ownerId);
    }
    
    @Override
    public List<TaskEntity> findOverdueTasks(LocalDateTime currentTime) {
        return jpaRepository.findOverdueTasks(currentTime);
    }

    @Override
    public Optional<TaskEntity> findByIdAndOwnerId(Long id, Long ownerId) {
        return jpaRepository.findByIdAndOwnerId(id, ownerId);
    }

    @Override
    public TaskEntity save(TaskEntity task) {
        return jpaRepository.save(task);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
