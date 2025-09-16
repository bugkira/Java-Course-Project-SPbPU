package spbstu.TasksApplication.repository.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import spbstu.TasksApplication.model.Task;
import spbstu.TasksApplication.repository.TaskRepository;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Profile("inmemory")
public class InMemoryTaskRepository implements TaskRepository {
    private final Map<Long, Task> tasks = new HashMap<>();
    private final AtomicLong taskIdCounter = new AtomicLong(1);

    @Override
    public List<Task> findByUserIdAndIsDeletedFalse(Long userId) {
        List<Task> result = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task.getUserId().equals(userId) && !task.getIsDeleted()) {
                result.add(task);
            }
        }
        return result;
    }

    @Override
    public List<Task> findByUserIdAndIsCompletedFalseAndIsDeletedFalse(Long userId) {
        List<Task> result = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task.getUserId().equals(userId) && !task.getIsCompleted() && !task.getIsDeleted()) {
                result.add(task);
            }
        }
        return result;
    }

    @Override
    public Optional<Task> findByIdAndDeletedFalse(Long id) {
        Task task = tasks.get(id);
        if (task != null && !task.getIsDeleted()) {
            return Optional.of(task);
        }
        return Optional.empty();
    }

    @Override
    public Task save(Task task) {
        if (task.getTaskId() == null) {
            task.setTaskId(taskIdCounter.getAndIncrement());
        }
        tasks.put(task.getTaskId(), task);
        return task;
    }
}
