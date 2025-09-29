package org.example.taskmanager.cache;

import static org.assertj.core.api.Assertions.assertThat;
import org.example.taskmanager.service.TaskManagementService;
import org.example.taskmanager.service.UserManagementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("docker")
class CacheIntegrationTest {

    @Autowired
    private TaskManagementService taskManagementService;

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private CacheManager cacheManager;

    @Test
    void shouldHaveCacheManagerConfigured() {
        // Verify that cache manager is properly configured
        assertThat(cacheManager).isNotNull();
        assertThat(cacheManager.getCacheNames()).contains("tasks", "users", "notifications");
    }

    @Test
    void shouldCacheEmptyTaskList() {
        // Given - create a user first
        String uniqueId = String.valueOf(System.currentTimeMillis());
        var user = org.example.taskmanager.domain.UserEntity.builder()
                .login("cacheuser" + uniqueId)
                .emailAddress("cache" + uniqueId + "@example.com")
                .passwordHash("password123")
                .build();
        var createdUser = userManagementService.createUserAccount(user);

        // Clear cache to ensure fresh start
        cacheManager.getCache("tasks").clear();

        // When - first call should hit database
        var firstCall = taskManagementService.getAllUserTasks(createdUser.getId());

        // Then - verify cache is populated
        assertThat(firstCall).isEmpty();

        // Verify cache contains the data (even empty list)
        assertThat(cacheManager.getCache("tasks").get("all-tasks-" + createdUser.getId())).isNotNull();
    }

    @Test
    void shouldCacheUserAuthentication() {
        // Given - create a user
        String uniqueId = String.valueOf(System.currentTimeMillis());
        var user = org.example.taskmanager.domain.UserEntity.builder()
                .login("cacheauth" + uniqueId)
                .emailAddress("cacheauth" + uniqueId + "@example.com")
                .passwordHash("password123")
                .build();
        userManagementService.createUserAccount(user);

        // Clear cache to ensure fresh start
        cacheManager.getCache("users").clear();

        // When - first authentication call
        var authenticatedUser = userManagementService.authenticateUser(user.getLogin(), user.getPasswordHash());

        // Then - verify cache is populated
        assertThat(authenticatedUser).isNotNull();
        assertThat(authenticatedUser.getLogin()).isEqualTo(user.getLogin());

        // Verify cache contains the user
        assertThat(cacheManager.getCache("users").get("user-" + user.getLogin())).isNotNull();
    }
}
