package spbstu.TasksApplication.repository.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spbstu.TasksApplication.model.Task;
import spbstu.TasksApplication.repository.TaskRepository;

import java.util.List;

@Repository
@Profile("h2")
public interface JpaTaskRepository extends JpaRepository<Task, Long>, TaskRepository {
    List<Task> findByUserIdAndIsDeletedFalse(Long userId);
    List<Task> findByUserIdAndIsCompletedFalseAndIsDeletedFalse(Long userId);
}
