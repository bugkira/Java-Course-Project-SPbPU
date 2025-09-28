package org.example.taskmanager.repository.memory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.example.taskmanager.domain.TaskEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("inmemory")
class MemoryTaskRepositoryTest {

    private MemoryTaskRepository repository;
    private TaskEntity task1;
    private TaskEntity task2;
    private TaskEntity task3;
    private TaskEntity removedTask;
    private Long ownerId1;
    private Long ownerId2;

    @BeforeEach
    void setUp() {
        repository = new MemoryTaskRepository();
        ownerId1 = 1L;
        ownerId2 = 2L;

        task1 = TaskEntity.builder()
                .name("Task 1")
                .details("Details 1")
                .ownerId(ownerId1)
                .dueDate(LocalDateTime.now().plusDays(1))
                .finished(false)
                .removed(false)
                .build();

        task2 = TaskEntity.builder()
                .name("Task 2")
                .details("Details 2")
                .ownerId(ownerId1)
                .dueDate(LocalDateTime.now().plusDays(2))
                .finished(true)
                .removed(false)
                .build();

        task3 = TaskEntity.builder()
                .name("Task 3")
                .details("Details 3")
                .ownerId(ownerId2)
                .dueDate(LocalDateTime.now().plusDays(3))
                .finished(false)
                .removed(false)
                .build();

        removedTask = TaskEntity.builder()
                .name("Removed Task")
                .details("Removed details")
                .ownerId(ownerId1)
                .dueDate(LocalDateTime.now().plusDays(1))
                .finished(false)
                .removed(true)
                .build();

        // Store test data
        repository.store(task1);
        repository.store(task2);
        repository.store(task3);
        repository.store(removedTask);
    }

    @Test
    void testFindAllByOwnerAndNotRemoved_Success() {
        // When
        List<TaskEntity> result = repository.findAllByOwnerAndNotRemoved(ownerId1);

        // Then
        assertEquals(2, result.size());
        assertTrue(result.contains(task1));
        assertTrue(result.contains(task2));
        assertFalse(result.contains(removedTask));
    }

    @Test
    void testFindAllByOwnerAndNotRemoved_EmptyResult() {
        // Given
        Long nonExistentOwnerId = 999L;

        // When
        List<TaskEntity> result = repository.findAllByOwnerAndNotRemoved(nonExistentOwnerId);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindAllByOwnerAndNotRemoved_DifferentOwner() {
        // When
        List<TaskEntity> result = repository.findAllByOwnerAndNotRemoved(ownerId2);

        // Then
        assertEquals(1, result.size());
        assertTrue(result.contains(task3));
    }

    @Test
    void testFindPendingByOwnerAndNotRemoved_Success() {
        // When
        List<TaskEntity> result = repository.findPendingByOwnerAndNotRemoved(ownerId1);

        // Then
        assertEquals(1, result.size());
        assertTrue(result.contains(task1));
        assertFalse(result.contains(task2)); // task2 is finished
        assertFalse(result.contains(removedTask)); // removedTask is removed
    }

    @Test
    void testFindPendingByOwnerAndNotRemoved_EmptyResult() {
        // Given
        Long nonExistentOwnerId = 999L;

        // When
        List<TaskEntity> result = repository.findPendingByOwnerAndNotRemoved(nonExistentOwnerId);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindPendingByOwnerAndNotRemoved_AllFinished() {
        // Given - all tasks for ownerId1 are finished or removed
        TaskEntity finishedTask = TaskEntity.builder()
                .name("Finished Task")
                .details("Finished details")
                .ownerId(ownerId1)
                .dueDate(LocalDateTime.now().plusDays(1))
                .finished(true)
                .removed(false)
                .build();
        repository.store(finishedTask);

        // When
        List<TaskEntity> result = repository.findPendingByOwnerAndNotRemoved(ownerId1);

        // Then
        assertEquals(1, result.size()); // Only task1 is pending
        assertTrue(result.contains(task1));
    }

    @Test
    void testFindByIdAndNotRemoved_Success() {
        // Given
        Long taskId = task1.getId();

        // When
        Optional<TaskEntity> result = repository.findByIdAndNotRemoved(taskId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(task1, result.get());
    }

    @Test
    void testFindByIdAndNotRemoved_TaskNotFound() {
        // Given
        Long nonExistentTaskId = 999L;

        // When
        Optional<TaskEntity> result = repository.findByIdAndNotRemoved(nonExistentTaskId);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testFindByIdAndNotRemoved_TaskRemoved() {
        // Given
        Long removedTaskId = removedTask.getId();

        // When
        Optional<TaskEntity> result = repository.findByIdAndNotRemoved(removedTaskId);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testStore_NewTask() {
        // Given
        TaskEntity newTask = TaskEntity.builder()
                .name("New Task")
                .details("New details")
                .ownerId(ownerId1)
                .dueDate(LocalDateTime.now().plusDays(1))
                .finished(false)
                .removed(false)
                .build();

        // When
        TaskEntity result = repository.store(newTask);

        // Then
        assertNotNull(result.getId());
        assertEquals("New Task", result.getName());
        assertEquals(ownerId1, result.getOwnerId());
        
        // Verify it can be found
        Optional<TaskEntity> foundTask = repository.findByIdAndNotRemoved(result.getId());
        assertTrue(foundTask.isPresent());
        assertEquals(result, foundTask.get());
    }

    @Test
    void testStore_UpdateTask() {
        // Given
        TaskEntity existingTask = repository.store(task1);
        existingTask.setName("Updated Task Name");
        existingTask.setFinished(true);

        // When
        TaskEntity result = repository.store(existingTask);

        // Then
        assertEquals(existingTask.getId(), result.getId());
        assertEquals("Updated Task Name", result.getName());
        assertTrue(result.getFinished());
        
        // Verify the update
        Optional<TaskEntity> foundTask = repository.findByIdAndNotRemoved(result.getId());
        assertTrue(foundTask.isPresent());
        assertEquals("Updated Task Name", foundTask.get().getName());
        assertTrue(foundTask.get().getFinished());
    }

    @Test
    void testStore_MultipleTasks() {
        // Given
        TaskEntity taskA = TaskEntity.builder()
                .name("Task A")
                .details("Details A")
                .ownerId(ownerId1)
                .dueDate(LocalDateTime.now().plusDays(1))
                .build();

        TaskEntity taskB = TaskEntity.builder()
                .name("Task B")
                .details("Details B")
                .ownerId(ownerId1)
                .dueDate(LocalDateTime.now().plusDays(2))
                .build();

        // When
        TaskEntity resultA = repository.store(taskA);
        TaskEntity resultB = repository.store(taskB);

        // Then
        assertNotNull(resultA.getId());
        assertNotNull(resultB.getId());
        assertNotEquals(resultA.getId(), resultB.getId());
        
        // Verify both can be found
        List<TaskEntity> allTasks = repository.findAllByOwnerAndNotRemoved(ownerId1);
        assertTrue(allTasks.size() >= 4); // task1, task2, taskA, taskB
    }

    @Test
    void testIdGeneration() {
        // Given
        TaskEntity task1 = TaskEntity.builder()
                .name("Task 1")
                .details("Details 1")
                .ownerId(ownerId1)
                .dueDate(LocalDateTime.now().plusDays(1))
                .build();

        TaskEntity task2 = TaskEntity.builder()
                .name("Task 2")
                .details("Details 2")
                .ownerId(ownerId1)
                .dueDate(LocalDateTime.now().plusDays(2))
                .build();

        // When
        TaskEntity result1 = repository.store(task1);
        TaskEntity result2 = repository.store(task2);

        // Then
        assertNotNull(result1.getId());
        assertNotNull(result2.getId());
        assertTrue(result2.getId() > result1.getId());
    }
}
