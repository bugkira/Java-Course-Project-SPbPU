package org.example.taskmanager.integration;

import java.time.LocalDateTime;
import java.util.List;

import org.example.taskmanager.domain.NotificationEntity;
import org.example.taskmanager.domain.TaskEntity;
import org.example.taskmanager.domain.UserEntity;
import org.example.taskmanager.repository.NotificationDataRepository;
import org.example.taskmanager.repository.TaskDataRepository;
import org.example.taskmanager.repository.UserDataRepository;
import org.example.taskmanager.service.TaskSchedulerService;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles({"messaging", "scheduling"})
@TestPropertySource(properties = {
    "spring.task.scheduling.enabled=true",
    "spring.task.execution.enabled=true"
})
class TaskSchedulerIntegrationTest {

    @Autowired
    private TaskSchedulerService taskSchedulerService;

    @Autowired
    private TaskDataRepository taskRepository;

    @Autowired
    private UserDataRepository userRepository;

    @Autowired
    private NotificationDataRepository notificationRepository;

    private UserEntity testUser;
    private TaskEntity overdueTask;

    @BeforeEach
    void setUp() {
        // Create test user with unique login
        String uniqueLogin = "schedulertest_" + System.currentTimeMillis();
        String uniqueEmail = "scheduler_" + System.currentTimeMillis() + "@test.com";
        testUser = UserEntity.builder()
                .login(uniqueLogin)
                .passwordHash("hashedpassword")
                .emailAddress(uniqueEmail)
                .build();
        testUser = userRepository.save(testUser);
        
        // Ensure user was saved properly
        assertNotNull(testUser, "Test user should be saved successfully");
        assertNotNull(testUser.getId(), "Test user should have an ID after saving");

        // Create overdue task
        overdueTask = TaskEntity.builder()
                .name("Scheduler Test Task")
                .details("This task is overdue for testing")
                .dueDate(LocalDateTime.now().minusHours(2))
                .ownerId(testUser.getId())
                .finished(false)
                .removed(false)
                .build();
        overdueTask = taskRepository.save(overdueTask);
        
        // Ensure task was saved properly
        assertNotNull(overdueTask, "Overdue task should be saved successfully");
        assertNotNull(overdueTask.getId(), "Overdue task should have an ID after saving");
    }

    @Test
    void checkOverdueTasks_ShouldCreateNotifications() throws InterruptedException {
        // Given
        List<NotificationEntity> initialNotifications = notificationRepository.findAllByRecipientId(testUser.getId());

        // When
        taskSchedulerService.checkOverdueTasks();

        // Wait a bit for async processing
        Thread.sleep(1000);

        // Then
        List<NotificationEntity> finalNotifications = notificationRepository.findAllByRecipientId(testUser.getId());
        assertTrue(finalNotifications.size() > initialNotifications.size(), 
                "Should have created notifications for overdue tasks");
    }

    @Test
    void checkOverdueTasksBusinessHours_ShouldCreateNotifications() throws InterruptedException {
        // Given
        List<NotificationEntity> initialNotifications = notificationRepository.findAllByRecipientId(testUser.getId());

        // When
        taskSchedulerService.checkOverdueTasksBusinessHours();

        // Wait a bit for async processing
        Thread.sleep(1000);

        // Then
        List<NotificationEntity> finalNotifications = notificationRepository.findAllByRecipientId(testUser.getId());
        assertTrue(finalNotifications.size() > initialNotifications.size(), 
                "Should have created notifications for overdue tasks during business hours");
    }

    @Test
    void findOverdueTasks_ShouldReturnCorrectTasks() {
        // When
        List<TaskEntity> overdueTasks = taskRepository.findOverdueTasks(LocalDateTime.now());

        // Then
        assertFalse(overdueTasks.isEmpty(), "Should find overdue tasks");
        assertTrue(overdueTasks.stream().anyMatch(task -> task.getId().equals(overdueTask.getId())),
                "Should include the test overdue task");
    }

    @Test
    void dailyCleanup_ShouldCompleteWithoutErrors() {
        // When & Then
        assertDoesNotThrow(() -> taskSchedulerService.dailyCleanup());
    }
}
