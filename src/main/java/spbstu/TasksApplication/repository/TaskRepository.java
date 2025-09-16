package spbstu.TasksApplication.repository;

import spbstu.TasksApplication.model.Task;
import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    List<Task> findByUserIdAndIsDeletedFalse(Long userId);
    List<Task> findByUserIdAndIsCompletedFalseAndIsDeletedFalse(Long userId);
    Optional<Task> findByIdAndDeletedFalse(Long id);
    Task save(Task task);
}
