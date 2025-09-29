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
@ActiveProfiles("postgresql")
class PostgreSQLProfileIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    void shouldCreateUserWithPostgreSQL() {
        // Given - create unique user for this test
        String uniqueId = String.valueOf(System.currentTimeMillis());
        UserEntity user = UserEntity.builder()
                .login("postgresqluser" + uniqueId)
                .emailAddress("postgresql" + uniqueId + "@example.com")
                .passwordHash("password123")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserEntity> userRequest = new HttpEntity<>(user, headers);

        // When
        ResponseEntity<String> userResponse = restTemplate.exchange(
                getBaseUrl() + "/api/users/register",
                HttpMethod.POST,
                userRequest,
                String.class
        );

        // Then
        assertThat(userResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(userResponse.getBody()).contains("postgresqluser" + uniqueId);
        assertThat(userResponse.getBody()).contains("postgresql" + uniqueId + "@example.com");
    }

    @Test
    void shouldCreateTaskWithPostgreSQL() {
        // Given
        TaskEntity task = TaskEntity.builder()
                .name("PostgreSQL Task")
                .details("Task created with PostgreSQL database")
                .ownerId(1L)
                .dueDate(LocalDateTime.of(2025, 12, 31, 23, 59, 59))
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
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
        assertThat(taskResponse.getBody()).contains("PostgreSQL Task");
        assertThat(taskResponse.getBody()).contains("Task created with PostgreSQL database");
    }

    @Test
    void shouldRetrieveNotificationsFromPostgreSQL() {
        // When - test GET endpoint for notifications
        ResponseEntity<String> notificationsResponse = restTemplate.exchange(
                getBaseUrl() + "/api/notifications/1",
                HttpMethod.GET,
                null,
                String.class
        );

        // Then
        assertThat(notificationsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        // Response should be valid JSON array (even if empty)
        assertThat(notificationsResponse.getBody()).isNotNull();
    }

    @Test
    void shouldRetrieveTasksFromPostgreSQL() {
        // When
        ResponseEntity<String> tasksResponse = restTemplate.exchange(
                getBaseUrl() + "/api/tasks/user/1",
                HttpMethod.GET,
                null,
                String.class
        );

        // Then
        assertThat(tasksResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        // Response should be valid JSON array (even if empty)
        assertThat(tasksResponse.getBody()).isNotNull();
    }

}
