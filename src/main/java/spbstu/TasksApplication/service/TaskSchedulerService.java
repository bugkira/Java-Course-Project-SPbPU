package spbstu.TasksApplication.service;

import spbstu.TasksApplication.model.Task;

import java.util.List;

public interface TaskSchedulerService {
    void checkOverdueTasks();
    void processOverdueTask(Task task);
    List<Task> findOverdueTasks();
}
