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
class BasicIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    void shouldCreateUserSuccessfully() throws Exception {
        // Given - create unique user for this test
        String uniqueId = String.valueOf(System.currentTimeMillis());
        UserEntity user = UserEntity.builder()
                .login("testuser" + uniqueId)
                .emailAddress("test" + uniqueId + "@example.com")
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
        assertThat(response.getBody()).contains("testuser" + uniqueId);
        assertThat(response.getBody()).contains("test" + uniqueId + "@example.com");
    }

    @Test
    void shouldCreateTaskSuccessfully() throws Exception {
        // Given
        TaskEntity task = TaskEntity.builder()
                .name("Test Task")
                .details("Test task details")
                .ownerId(1L)
                .dueDate(LocalDateTime.of(2025, 12, 31, 23, 59, 59))
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TaskEntity> request = new HttpEntity<>(task, headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/api/tasks",
                HttpMethod.POST,
                request,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Test Task");
        assertThat(response.getBody()).contains("Test task details");
    }

    @Test
    void shouldGetUserTasksSuccessfully() throws Exception {
        // When
        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/api/tasks/user/1",
                HttpMethod.GET,
                null,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).startsWith("[");
        assertThat(response.getBody()).endsWith("]");
    }

    @Test
    void shouldGetPendingUserTasksSuccessfully() throws Exception {
        // When
        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/api/tasks/user/1/active",
                HttpMethod.GET,
                null,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).startsWith("[");
        assertThat(response.getBody()).endsWith("]");
    }

    @Test
    void shouldGetUserNotificationsSuccessfully() throws Exception {
        // When
        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/api/notifications/1",
                HttpMethod.GET,
                null,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).startsWith("[");
        assertThat(response.getBody()).endsWith("]");
    }

    @Test
    void shouldGetPendingUserNotificationsSuccessfully() throws Exception {
        // When
        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/api/notifications/1/unread",
                HttpMethod.GET,
                null,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).startsWith("[");
        assertThat(response.getBody()).endsWith("]");
    }

    @Test
    void shouldReturn404ForNonExistentEndpoint() throws Exception {
        // When
        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/api/nonexistent",
                HttpMethod.GET,
                null,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldHandleInvalidUserLogin() throws Exception {
        // When
        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/api/users/login?login=nonexistent&password=wrong",
                HttpMethod.GET,
                null,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
