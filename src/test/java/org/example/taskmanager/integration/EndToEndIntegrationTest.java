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
class EndToEndIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    void shouldCompleteFullUserTaskWorkflow() throws Exception {
        // Step 1: Create a user
        String uniqueId = String.valueOf(System.currentTimeMillis());
        UserEntity user = UserEntity.builder()
                .login("workflowuser" + uniqueId)
                .emailAddress("workflow" + uniqueId + "@example.com")
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
        assertThat(userResponse.getBody()).contains("workflowuser" + uniqueId);

        // Step 2: Create multiple tasks for the user
        TaskEntity task1 = TaskEntity.builder()
                .name("First Task")
                .details("This is the first task")
                .ownerId(1L)
                .dueDate(LocalDateTime.of(2025, 12, 31, 23, 59, 59))
                .build();

        TaskEntity task2 = TaskEntity.builder()
                .name("Second Task")
                .details("This is the second task")
                .ownerId(1L)
                .dueDate(LocalDateTime.of(2025, 12, 31, 23, 59, 59))
                .build();

        HttpEntity<TaskEntity> task1Request = new HttpEntity<>(task1, headers);
        HttpEntity<TaskEntity> task2Request = new HttpEntity<>(task2, headers);

        ResponseEntity<String> task1Response = restTemplate.exchange(
                getBaseUrl() + "/api/tasks",
                HttpMethod.POST,
                task1Request,
                String.class
        );

        ResponseEntity<String> task2Response = restTemplate.exchange(
                getBaseUrl() + "/api/tasks",
                HttpMethod.POST,
                task2Request,
                String.class
        );

        assertThat(task1Response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(task2Response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(task1Response.getBody()).contains("First Task");
        assertThat(task2Response.getBody()).contains("Second Task");

        // Step 3: Get all user tasks
        ResponseEntity<String> allTasksResponse = restTemplate.exchange(
                getBaseUrl() + "/api/tasks/user/1",
                HttpMethod.GET,
                null,
                String.class
        );

        assertThat(allTasksResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(allTasksResponse.getBody()).contains("First Task");
        assertThat(allTasksResponse.getBody()).contains("Second Task");

        // Step 4: Get pending tasks only
        ResponseEntity<String> pendingTasksResponse = restTemplate.exchange(
                getBaseUrl() + "/api/tasks/user/1/active",
                HttpMethod.GET,
                null,
                String.class
        );

        assertThat(pendingTasksResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(pendingTasksResponse.getBody()).contains("First Task");
        assertThat(pendingTasksResponse.getBody()).contains("Second Task");

        // Step 5: Verify both tasks exist
        ResponseEntity<String> allTasksAfterCreationResponse = restTemplate.exchange(
                getBaseUrl() + "/api/tasks/user/1",
                HttpMethod.GET,
                null,
                String.class
        );

        assertThat(allTasksAfterCreationResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(allTasksAfterCreationResponse.getBody()).contains("First Task");
        assertThat(allTasksAfterCreationResponse.getBody()).contains("Second Task");

        // Step 7: Test user login
        ResponseEntity<String> loginResponse = restTemplate.exchange(
                getBaseUrl() + "/api/users/login?login=workflowuser" + uniqueId + "&password=password123",
                HttpMethod.GET,
                null,
                String.class
        );

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody()).contains("workflowuser" + uniqueId);

        // Step 8: Test notifications (should be empty for now)
        ResponseEntity<String> notificationsResponse = restTemplate.exchange(
                getBaseUrl() + "/api/notifications/1",
                HttpMethod.GET,
                null,
                String.class
        );

        assertThat(notificationsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(notificationsResponse.getBody()).isEqualTo("[]");
    }

    @Test
    void shouldHandleErrorScenarios() throws Exception {
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

        // Test task creation for non-existent user
        TaskEntity task = TaskEntity.builder()
                .name("Task for Non-existent User")
                .details("This should fail")
                .ownerId(999L) // Non-existent user ID
                .dueDate(LocalDateTime.of(2025, 12, 31, 23, 59, 59))
                .build();

        HttpEntity<TaskEntity> taskRequest = new HttpEntity<>(task, headers);

        ResponseEntity<String> taskResponse = restTemplate.exchange(
                getBaseUrl() + "/api/tasks",
                HttpMethod.POST,
                taskRequest,
                String.class
        );

        assertThat(taskResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        // Test getting tasks for non-existent user
        ResponseEntity<String> getTasksResponse = restTemplate.exchange(
                getBaseUrl() + "/api/tasks/user/999",
                HttpMethod.GET,
                null,
                String.class
        );

        assertThat(getTasksResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getTasksResponse.getBody()).isEqualTo("[]");
    }
}
