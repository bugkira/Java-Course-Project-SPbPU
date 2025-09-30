package org.example.taskmanager.repository.memory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.example.taskmanager.domain.TaskEntity;
import org.example.taskmanager.repository.TaskDataRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("inmemory")
public class MemoryTaskRepository implements TaskDataRepository {
    private final Map<Long, TaskEntity> storage = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public List<TaskEntity> findAllByOwnerId(Long ownerId) {
        return storage.values().stream()
                .filter(task -> task.getOwnerId().equals(ownerId) && !task.getRemoved())
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskEntity> findPendingTasksByOwnerId(Long ownerId) {
        return storage.values().stream()
                .filter(task -> task.getOwnerId().equals(ownerId) 
                    && !task.getFinished() 
                    && !task.getRemoved())
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskEntity> findOverdueTasks(LocalDateTime currentTime) {
        return storage.values().stream()
                .filter(task -> task.getDueDate().isBefore(currentTime)
                    && !task.getFinished() 
                    && !task.getRemoved())
                .collect(Collectors.toList());
    }

    @Override
    public Optional<TaskEntity> findByIdAndOwnerId(Long id, Long ownerId) {
        TaskEntity task = storage.get(id);
        if (task != null && !task.getRemoved() && task.getOwnerId().equals(ownerId)) {
            return Optional.of(task);
        }
        return Optional.empty();
    }

    @Override
    public TaskEntity save(TaskEntity task) {
        if (task.getId() == null) {
            task.setId(idGenerator.getAndIncrement());
        }
        storage.put(task.getId(), task);
        return task;
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }
}
