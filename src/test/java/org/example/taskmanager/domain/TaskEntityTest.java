package org.example.taskmanager.domain;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class TaskEntityTest {

    @Test
    void testBuilder_Success() {
        // Given
        LocalDateTime dueDate = LocalDateTime.now().plusDays(1);
        Long ownerId = 1L;

        // When
        TaskEntity task = TaskEntity.builder()
                .name("Test Task")
                .details("Test details")
                .ownerId(ownerId)
                .dueDate(dueDate)
                .build();

        // Then
        assertNotNull(task);
        assertEquals("Test Task", task.getName());
        assertEquals("Test details", task.getDetails());
        assertEquals(ownerId, task.getOwnerId());
        assertEquals(dueDate, task.getDueDate());
        assertFalse(task.getFinished());
        assertFalse(task.getRemoved());
        assertNotNull(task.getCreateTime());
    }

    @Test
    void testBuilder_WithAllFields() {
        // Given
        Long id = 1L;
        String name = "Complete Task";
        String details = "Complete details";
        LocalDateTime createTime = LocalDateTime.now().minusHours(1);
        LocalDateTime dueDate = LocalDateTime.now().plusDays(1);
        Long ownerId = 1L;
        Boolean finished = true;
        Boolean removed = false;

        // When
        TaskEntity task = TaskEntity.builder()
                .id(id)
                .name(name)
                .details(details)
                .createTime(createTime)
                .dueDate(dueDate)
                .ownerId(ownerId)
                .finished(finished)
                .removed(removed)
                .build();

        // Then
        assertEquals(id, task.getId());
        assertEquals(name, task.getName());
        assertEquals(details, task.getDetails());
        assertEquals(createTime, task.getCreateTime());
        assertEquals(dueDate, task.getDueDate());
        assertEquals(ownerId, task.getOwnerId());
        assertEquals(finished, task.getFinished());
        assertEquals(removed, task.getRemoved());
    }

    @Test
    void testBuilder_DefaultValues() {
        // Given
        String name = "Default Task";
        String details = "Default details";
        Long ownerId = 1L;
        LocalDateTime dueDate = LocalDateTime.now().plusDays(1);

        // When
        TaskEntity task = TaskEntity.builder()
                .name(name)
                .details(details)
                .ownerId(ownerId)
                .dueDate(dueDate)
                .build();

        // Then
        assertNull(task.getId());
        assertEquals(name, task.getName());
        assertEquals(details, task.getDetails());
        assertEquals(ownerId, task.getOwnerId());
        assertEquals(dueDate, task.getDueDate());
        assertFalse(task.getFinished());
        assertFalse(task.getRemoved());
        assertNotNull(task.getCreateTime());
    }

    @Test
    void testBuilder_DefaultCreateTime() {
        // Given
        LocalDateTime beforeCreation = LocalDateTime.now();
        
        TaskEntity task = TaskEntity.builder()
                .name("Test Task")
                .details("Test details")
                .ownerId(1L)
                .dueDate(LocalDateTime.now().plusDays(1))
                .build();

        LocalDateTime afterCreation = LocalDateTime.now();

        // Then
        assertNotNull(task.getCreateTime());
        assertTrue(task.getCreateTime().isAfter(beforeCreation.minusSeconds(1)));
        assertTrue(task.getCreateTime().isBefore(afterCreation.plusSeconds(1)));
    }

    @Test
    void testEqualsAndHashCode_SameObjects() {
        // Given
        LocalDateTime dueDate = LocalDateTime.now().plusDays(1);
        LocalDateTime createTime = LocalDateTime.now();
        
        TaskEntity task1 = TaskEntity.builder()
                .id(1L)
                .name("Test Task")
                .details("Test details")
                .createTime(createTime)
                .ownerId(1L)
                .dueDate(dueDate)
                .finished(false)
                .removed(false)
                .build();

        TaskEntity task2 = TaskEntity.builder()
                .id(1L)
                .name("Test Task")
                .details("Test details")
                .createTime(createTime)
                .ownerId(1L)
                .dueDate(dueDate)
                .finished(false)
                .removed(false)
                .build();

        // When & Then
        assertEquals(task1, task2);
        assertEquals(task1.hashCode(), task2.hashCode());
    }

    @Test
    void testEqualsAndHashCode_DifferentObjects() {
        // Given
        TaskEntity task1 = TaskEntity.builder()
                .id(1L)
                .name("Task 1")
                .details("Details 1")
                .ownerId(1L)
                .dueDate(LocalDateTime.now().plusDays(1))
                .build();

        TaskEntity task2 = TaskEntity.builder()
                .id(2L)
                .name("Task 2")
                .details("Details 2")
                .ownerId(1L)
                .dueDate(LocalDateTime.now().plusDays(2))
                .build();

        // When & Then
        assertNotEquals(task1, task2);
        assertNotEquals(task1.hashCode(), task2.hashCode());
    }

    @Test
    void testEqualsAndHashCode_DifferentNames() {
        // Given
        LocalDateTime dueDate = LocalDateTime.now().plusDays(1);
        
        TaskEntity task1 = TaskEntity.builder()
                .id(1L)
                .name("Task 1")
                .details("Same details")
                .ownerId(1L)
                .dueDate(dueDate)
                .build();

        TaskEntity task2 = TaskEntity.builder()
                .id(1L)
                .name("Task 2")
                .details("Same details")
                .ownerId(1L)
                .dueDate(dueDate)
                .build();

        // When & Then
        assertNotEquals(task1, task2);
        assertNotEquals(task1.hashCode(), task2.hashCode());
    }

    @Test
    void testEqualsAndHashCode_DifferentOwners() {
        // Given
        LocalDateTime dueDate = LocalDateTime.now().plusDays(1);
        
        TaskEntity task1 = TaskEntity.builder()
                .id(1L)
                .name("Same Task")
                .details("Same details")
                .ownerId(1L)
                .dueDate(dueDate)
                .build();

        TaskEntity task2 = TaskEntity.builder()
                .id(1L)
                .name("Same Task")
                .details("Same details")
                .ownerId(2L)
                .dueDate(dueDate)
                .build();

        // When & Then
        assertNotEquals(task1, task2);
        assertNotEquals(task1.hashCode(), task2.hashCode());
    }

    @Test
    void testToString() {
        // Given
        TaskEntity task = TaskEntity.builder()
                .id(1L)
                .name("Test Task")
                .details("Test details")
                .ownerId(1L)
                .dueDate(LocalDateTime.now().plusDays(1))
                .finished(false)
                .removed(false)
                .build();

        // When
        String toString = task.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("TaskEntity"));
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("name=Test Task"));
        assertTrue(toString.contains("details=Test details"));
        assertTrue(toString.contains("ownerId=1"));
        assertTrue(toString.contains("finished=false"));
        assertTrue(toString.contains("removed=false"));
    }

    @Test
    void testSetters() {
        // Given
        TaskEntity task = TaskEntity.builder()
                .name("Original Task")
                .details("Original details")
                .ownerId(1L)
                .dueDate(LocalDateTime.now().plusDays(1))
                .build();

        // When
        task.setId(999L);
        task.setName("Updated Task");
        task.setDetails("Updated details");
        task.setFinished(true);
        task.setRemoved(true);

        // Then
        assertEquals(999L, task.getId());
        assertEquals("Updated Task", task.getName());
        assertEquals("Updated details", task.getDetails());
        assertTrue(task.getFinished());
        assertTrue(task.getRemoved());
    }

    @Test
    void testNoArgsConstructor() {
        // When
        TaskEntity task = new TaskEntity();

        // Then
        assertNotNull(task);
        assertNull(task.getId());
        assertNull(task.getName());
        assertNull(task.getDetails());
        assertNotNull(task.getCreateTime()); // createTime is generated automatically
        assertNull(task.getDueDate());
        assertNull(task.getOwnerId());
        assertFalse(task.getFinished()); // finished has default value false
        assertFalse(task.getRemoved()); // removed has default value false
    }

    @Test
    void testAllArgsConstructor() {
        // Given
        Long id = 1L;
        String name = "Test Task";
        String details = "Test details";
        LocalDateTime createTime = LocalDateTime.now().minusHours(1);
        LocalDateTime dueDate = LocalDateTime.now().plusDays(1);
        Long ownerId = 1L;
        Boolean finished = true;
        Boolean removed = false;

        // When
        TaskEntity task = new TaskEntity(id, name, details, createTime, dueDate, ownerId, finished, removed);

        // Then
        assertEquals(id, task.getId());
        assertEquals(name, task.getName());
        assertEquals(details, task.getDetails());
        assertEquals(createTime, task.getCreateTime());
        assertEquals(dueDate, task.getDueDate());
        assertEquals(ownerId, task.getOwnerId());
        assertEquals(finished, task.getFinished());
        assertEquals(removed, task.getRemoved());
    }
}