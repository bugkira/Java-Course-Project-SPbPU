package org.example.taskmanager.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.taskmanager.domain.TaskEntity;
import org.example.taskmanager.domain.UserEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("docker")
@AutoConfigureWebMvc
class DockerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    void shouldCreateUserSuccessfully() throws Exception {
        // Given
        UserEntity user = UserEntity.builder()
                .login("testuser")
                .emailAddress("test@example.com")
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
        assertThat(response.getBody()).contains("testuser");
        assertThat(response.getBody()).contains("test@example.com");
        assertThat(response.getBody()).contains("password123");
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
        assertThat(response.getBody()).contains("ownerId\":1");
        assertThat(response.getBody()).contains("finished\":false");
        assertThat(response.getBody()).contains("removed\":false");
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
                getBaseUrl() + "/api/tasks/user/1/pending",
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
                getBaseUrl() + "/api/notifications/user/1",
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
                getBaseUrl() + "/api/notifications/user/1/pending",
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
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturnBadRequestForInvalidTaskData() throws Exception {
        // Given
        TaskEntity invalidTask = TaskEntity.builder()
                .name("") // Empty name should be invalid
                .ownerId(1L)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TaskEntity> request = new HttpEntity<>(invalidTask, headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                getBaseUrl() + "/api/tasks",
                HttpMethod.POST,
                request,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
