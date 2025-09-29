package org.example.taskmanager.integration;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import org.example.taskmanager.domain.TaskEntity;
import org.example.taskmanager.domain.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("docker")
class SimpleIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    void shouldCreateUserAndTaskSuccessfully() throws Exception {
        // Step 1: Create a user
        String uniqueId = String.valueOf(System.currentTimeMillis());
        UserEntity user = UserEntity.builder()
                .login("testuser" + uniqueId)
                .emailAddress("test" + uniqueId + "@example.com")
                .passwordHash("password123")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserEntity> userRequest = new HttpEntity<>(user, headers);

        ResponseEntity<String> userResponse = restTemplate.exchange(
                getBaseUrl() + "/api/users/register",
                HttpMethod.POST,
                userRequest,
                String.class
        );

        assertThat(userResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(userResponse.getBody()).contains("testuser" + uniqueId);
        assertThat(userResponse.getBody()).contains("test" + uniqueId + "@example.com");

        // Step 2: Create a task
        TaskEntity task = TaskEntity.builder()
                .name("Test Task")
                .details("Test task details")
                .ownerId(1L)
                .dueDate(LocalDateTime.of(2025, 12, 31, 23, 59, 59))
                .build();

        HttpEntity<TaskEntity> taskRequest = new HttpEntity<>(task, headers);

        ResponseEntity<String> taskResponse = restTemplate.exchange(
                getBaseUrl() + "/api/tasks",
                HttpMethod.POST,
                taskRequest,
                String.class
        );

        assertThat(taskResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(taskResponse.getBody()).contains("Test Task");
        assertThat(taskResponse.getBody()).contains("Test task details");

        // Step 3: Get user tasks
        ResponseEntity<String> getTasksResponse = restTemplate.exchange(
                getBaseUrl() + "/api/tasks/user/1",
                HttpMethod.GET,
                null,
                String.class
        );

        assertThat(getTasksResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getTasksResponse.getBody()).contains("Test Task");

        // Step 4: Get pending tasks
        ResponseEntity<String> getPendingTasksResponse = restTemplate.exchange(
                getBaseUrl() + "/api/tasks/user/1/active",
                HttpMethod.GET,
                null,
                String.class
        );

        assertThat(getPendingTasksResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getPendingTasksResponse.getBody()).contains("Test Task");

        // Step 5: Test user login
        ResponseEntity<String> loginResponse = restTemplate.exchange(
                getBaseUrl() + "/api/users/login?login=testuser" + uniqueId + "&password=password123",
                HttpMethod.GET,
                null,
                String.class
        );

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody()).contains("testuser" + uniqueId);

        // Step 6: Get notifications (should be empty)
        ResponseEntity<String> notificationsResponse = restTemplate.exchange(
                getBaseUrl() + "/api/notifications/1",
                HttpMethod.GET,
                null,
                String.class
        );

        assertThat(notificationsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(notificationsResponse.getBody()).isEqualTo("[]");

        // Step 7: Get pending notifications (should be empty)
        ResponseEntity<String> pendingNotificationsResponse = restTemplate.exchange(
                getBaseUrl() + "/api/notifications/1/unread",
                HttpMethod.GET,
                null,
                String.class
        );

        assertThat(pendingNotificationsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(pendingNotificationsResponse.getBody()).isEqualTo("[]");
    }

    @Test
    void shouldHandleInvalidRequests() throws Exception {
        // Test invalid user creation
        UserEntity invalidUser = UserEntity.builder()
                .login("") // Empty login should be invalid
                .emailAddress("invalid@example.com")
                .passwordHash("password123")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserEntity> request = new HttpEntity<>(invalidUser, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/api/users/register",
                HttpMethod.POST,
                request,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        // Test non-existent endpoint
        ResponseEntity<String> notFoundResponse = restTemplate.exchange(
                getBaseUrl() + "/api/nonexistent",
                HttpMethod.GET,
                null,
                String.class
        );

        assertThat(notFoundResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
