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
@ActiveProfiles("h2")
class H2ProfileIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    void shouldWorkWithH2Profile() throws Exception {
        // Given
        UserEntity user = UserEntity.builder()
                .login("h2user")
                .emailAddress("h2@example.com")
                .passwordHash("password123")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserEntity> request = new HttpEntity<>(user, headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/api/users/register",
                HttpMethod.POST,
                request,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("h2user");
    }

    @Test
    void shouldCreateAndRetrieveTaskWithH2Profile() throws Exception {
        // Given - Create user first
        UserEntity user = UserEntity.builder()
                .login("h2taskuser")
                .emailAddress("h2task@example.com")
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

        // Create task
        TaskEntity task = TaskEntity.builder()
                .name("H2 Task")
                .details("Task created with H2 profile")
                .ownerId(1L)
                .dueDate(LocalDateTime.of(2025, 12, 31, 23, 59, 59))
                .build();

        HttpEntity<TaskEntity> taskRequest = new HttpEntity<>(task, headers);

        // When
        ResponseEntity<String> taskResponse = restTemplate.exchange(
                getBaseUrl() + "/api/tasks",
                HttpMethod.POST,
                taskRequest,
                String.class
        );

        // Then
        assertThat(taskResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(taskResponse.getBody()).contains("H2 Task");

        // Verify task retrieval
        ResponseEntity<String> getTasksResponse = restTemplate.exchange(
                getBaseUrl() + "/api/tasks/user/1",
                HttpMethod.GET,
                null,
                String.class
        );

        assertThat(getTasksResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getTasksResponse.getBody()).contains("H2 Task");
    }

    @Test
    void shouldHandleTaskRemovalWithH2Profile() throws Exception {
        // Given - Create user and task first
        UserEntity user = UserEntity.builder()
                .login("h2removeuser")
                .emailAddress("h2remove@example.com")
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

        TaskEntity task = TaskEntity.builder()
                .name("Task to Remove")
                .details("This task will be removed")
                .ownerId(1L)
                .dueDate(LocalDateTime.of(2025, 12, 31, 23, 59, 59))
                .build();

        HttpEntity<TaskEntity> taskRequest = new HttpEntity<>(task, headers);

        ResponseEntity<TaskEntity> taskResponse = restTemplate.exchange(
                getBaseUrl() + "/api/tasks",
                HttpMethod.POST,
                taskRequest,
                TaskEntity.class
        );

        assertThat(taskResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        TaskEntity createdTask = taskResponse.getBody();
        assertThat(createdTask).isNotNull();
        assertThat(createdTask.getId()).isNotNull();

        // When - Remove task
        ResponseEntity<Void> removeResponse = restTemplate.exchange(
                getBaseUrl() + "/api/tasks/" + createdTask.getId() + "/user/1",
                HttpMethod.DELETE,
                null,
                Void.class
        );

        // Then
        assertThat(removeResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Verify task is not returned in user tasks
        ResponseEntity<String> getTasksResponse = restTemplate.exchange(
                getBaseUrl() + "/api/tasks/user/1",
                HttpMethod.GET,
                null,
                String.class
        );

        assertThat(getTasksResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        // Task should not be in the list (should be marked as removed)
        assertThat(getTasksResponse.getBody()).doesNotContain("Task to Remove");
    }
}
