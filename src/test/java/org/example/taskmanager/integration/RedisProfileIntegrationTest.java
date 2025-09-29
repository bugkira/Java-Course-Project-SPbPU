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
@ActiveProfiles("docker")
class RedisProfileIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    void shouldCreateUserWithRedisCache() {
        // Given - create unique user for this test
        String uniqueId = String.valueOf(System.currentTimeMillis());
        UserEntity user = UserEntity.builder()
                .login("redisuser" + uniqueId)
                .emailAddress("redis" + uniqueId + "@example.com")
                .passwordHash("password123")
                .build();

        HttpEntity<UserEntity> userRequest = new HttpEntity<>(user);

        // When - create user
        ResponseEntity<String> userResponse = restTemplate.exchange(
                getBaseUrl() + "/api/users/register",
                HttpMethod.POST,
                userRequest,
                String.class
        );

        // Then
        assertThat(userResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(userResponse.getBody()).contains("redisuser" + uniqueId);
        assertThat(userResponse.getBody()).contains("redis" + uniqueId + "@example.com");
    }

    @Test
    void shouldCreateTaskWithRedisCache() {
        // Given - create user first
        String uniqueId = String.valueOf(System.currentTimeMillis());
        UserEntity user = UserEntity.builder()
                .login("redistaskuser" + uniqueId)
                .emailAddress("redistask" + uniqueId + "@example.com")
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
                .name("Redis Cached Task")
                .details("This task should be cached in Redis")
                .ownerId(1L)
                .dueDate(java.time.LocalDateTime.of(2025, 12, 31, 23, 59, 59))
                .build();

        HttpEntity<TaskEntity> taskRequest = new HttpEntity<>(task);

        // When - create task
        ResponseEntity<String> taskResponse = restTemplate.exchange(
                getBaseUrl() + "/api/tasks",
                HttpMethod.POST,
                taskRequest,
                String.class
        );

        // Then
        assertThat(taskResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(taskResponse.getBody()).contains("Redis Cached Task");
    }

    @Test
    void shouldRetrieveTasksWithRedisCache() {
        // Given - create user first
        String uniqueId = String.valueOf(System.currentTimeMillis());
        UserEntity user = UserEntity.builder()
                .login("redisgetuser" + uniqueId)
                .emailAddress("redisget" + uniqueId + "@example.com")
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

        // When - get user tasks (should be cached in Redis)
        ResponseEntity<String> tasksResponse = restTemplate.exchange(
                getBaseUrl() + "/api/tasks/user/1/active",
                HttpMethod.GET,
                null,
                String.class
        );

        // Then
        assertThat(tasksResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        // Should return tasks (database contains data from previous tests)
        assertThat(tasksResponse.getBody()).isNotEmpty();
    }

    @Test
    void shouldAuthenticateUserWithRedisCache() {
        // Given - create user first
        String uniqueId = String.valueOf(System.currentTimeMillis());
        UserEntity user = UserEntity.builder()
                .login("redislogin" + uniqueId)
                .emailAddress("redislogin" + uniqueId + "@example.com")
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

        // When - authenticate user (should be cached in Redis)
        ResponseEntity<String> authResponse = restTemplate.exchange(
                getBaseUrl() + "/api/users/login?login=redislogin" + uniqueId + "&password=password123",
                HttpMethod.GET,
                null,
                String.class
        );

        // Then
        assertThat(authResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(authResponse.getBody()).contains("redislogin" + uniqueId);
    }
}
