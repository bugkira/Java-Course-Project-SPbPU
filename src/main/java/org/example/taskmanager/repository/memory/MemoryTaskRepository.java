package org.example.taskmanager.repository.memory;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.example.taskmanager.domain.TaskEntity;
import org.example.taskmanager.repository.TaskDataRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
@Profile("inmemory")
public class MemoryTaskRepository implements TaskDataRepository {
    private final Map<Long, TaskEntity> storage = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public List<TaskEntity> findAllByOwnerAndNotRemoved(Long ownerId) {
        return storage.values().stream()
                .filter(task -> task.getOwnerId().equals(ownerId) && !task.getRemoved())
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskEntity> findPendingByOwnerAndNotRemoved(Long ownerId) {
        return storage.values().stream()
                .filter(task -> task.getOwnerId().equals(ownerId) 
                    && !task.getFinished() 
                    && !task.getRemoved())
                .collect(Collectors.toList());
    }

    @Override
    public Optional<TaskEntity> findByIdAndNotRemoved(Long id) {
        TaskEntity task = storage.get(id);
        if (task != null && !task.getRemoved()) {
            return Optional.of(task);
        }
        return Optional.empty();
    }

    @Override
    public TaskEntity store(TaskEntity task) {
        if (task.getId() == null) {
            task.setId(idGenerator.getAndIncrement());
        }
        storage.put(task.getId(), task);
        return task;
    }
}
