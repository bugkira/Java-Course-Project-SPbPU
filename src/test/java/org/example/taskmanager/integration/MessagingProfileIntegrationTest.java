package org.example.taskmanager.integration;

import static org.assertj.core.api.Assertions.assertThat;
import org.example.taskmanager.domain.TaskEntity;
import org.example.taskmanager.domain.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("messaging")
class MessagingProfileIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    void shouldCreateTaskAndTriggerNotificationViaMessaging() {
        // Given - create user first
        String uniqueId = String.valueOf(System.currentTimeMillis());
        UserEntity user = UserEntity.builder()
                .login("messaginguser" + uniqueId)
                .emailAddress("messaging" + uniqueId + "@example.com")
                .passwordHash("password123")
                .build();

        HttpEntity<UserEntity> userRequest = new HttpEntity<>(user);
        ResponseEntity<String> userResponse = restTemplate.exchange(
                getBaseUrl() + "/api/users/register",
                HttpMethod.POST,
                userRequest,
                String.class
        );
        assertThat(userResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Create task
        TaskEntity task = TaskEntity.builder()
                .name("Messaging Test Task")
                .details("This task should trigger notification via messaging")
                .ownerId(1L)
                .dueDate(java.time.LocalDateTime.of(2025, 12, 31, 23, 59, 59))
                .build();

        HttpEntity<TaskEntity> taskRequest = new HttpEntity<>(task);

        // When - create task (should trigger message and create notification)
        ResponseEntity<String> taskResponse = restTemplate.exchange(
                getBaseUrl() + "/api/tasks",
                HttpMethod.POST,
                taskRequest,
                String.class
        );

        // Then
        assertThat(taskResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(taskResponse.getBody()).contains("Messaging Test Task");

        // Wait a bit for async message processing
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Check that notification was created via messaging
        ResponseEntity<String> notificationsResponse = restTemplate.exchange(
                getBaseUrl() + "/api/notifications/1",
                HttpMethod.GET,
                null,
                String.class
        );

        assertThat(notificationsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(notificationsResponse.getBody()).contains("New task created: Messaging Test Task");
    }
}
