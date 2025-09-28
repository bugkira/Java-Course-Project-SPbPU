package org.example.taskmanager.repository.memory;

import java.util.Optional;

import org.example.taskmanager.domain.UserEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("inmemory")
class MemoryUserRepositoryTest {

    private MemoryUserRepository repository;
    private UserEntity user1;
    private UserEntity user2;

    @BeforeEach
    void setUp() {
        repository = new MemoryUserRepository();

        user1 = UserEntity.builder()
                .login("user1")
                .passwordHash("password123")
                .emailAddress("user1@example.com")
                .build();

        user2 = UserEntity.builder()
                .login("user2")
                .passwordHash("password456")
                .emailAddress("user2@example.com")
                .build();

        // Store test data
        repository.store(user1);
        repository.store(user2);
    }

    @Test
    void testFindByLogin_Success() {
        // When
        Optional<UserEntity> result = repository.findByLogin("user1");

        // Then
        assertTrue(result.isPresent());
        assertEquals(user1, result.get());
        assertEquals("user1", result.get().getLogin());
        assertEquals("password123", result.get().getPasswordHash());
        assertEquals("user1@example.com", result.get().getEmailAddress());
    }

    @Test
    void testFindByLogin_NotFound() {
        // When
        Optional<UserEntity> result = repository.findByLogin("nonexistent");

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testFindByLogin_CaseSensitive() {
        // When
        Optional<UserEntity> result = repository.findByLogin("USER1");

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testStore_NewUser() {
        // Given
        UserEntity newUser = UserEntity.builder()
                .login("newuser")
                .passwordHash("newpassword123")
                .emailAddress("newuser@example.com")
                .build();

        // When
        UserEntity result = repository.store(newUser);

        // Then
        assertNotNull(result.getId());
        assertEquals("newuser", result.getLogin());
        assertEquals("newpassword123", result.getPasswordHash());
        assertEquals("newuser@example.com", result.getEmailAddress());

        // Verify it can be found
        Optional<UserEntity> foundUser = repository.findByLogin("newuser");
        assertTrue(foundUser.isPresent());
        assertEquals(result, foundUser.get());
    }

    @Test
    void testStore_UpdateUser() {
        // Given
        UserEntity existingUser = repository.store(user1);
        existingUser.setPasswordHash("updatedpassword");
        existingUser.setEmailAddress("updated@example.com");

        // When
        UserEntity result = repository.store(existingUser);

        // Then
        assertEquals(existingUser.getId(), result.getId());
        assertEquals("updatedpassword", result.getPasswordHash());
        assertEquals("updated@example.com", result.getEmailAddress());

        // Verify the update
        Optional<UserEntity> foundUser = repository.findByLogin("user1");
        assertTrue(foundUser.isPresent());
        assertEquals("updatedpassword", foundUser.get().getPasswordHash());
        assertEquals("updated@example.com", foundUser.get().getEmailAddress());
    }

    @Test
    void testCheckLoginExists_ExistingLogin() {
        // When
        boolean exists = repository.checkLoginExists("user1");

        // Then
        assertTrue(exists);
    }

    @Test
    void testCheckLoginExists_NonExistentLogin() {
        // When
        boolean exists = repository.checkLoginExists("nonexistent");

        // Then
        assertFalse(exists);
    }

    @Test
    void testCheckLoginExists_CaseSensitive() {
        // When
        boolean exists = repository.checkLoginExists("USER1");

        // Then
        assertFalse(exists);
    }

    @Test
    void testCheckEmailExists_ExistingEmail() {
        // When
        boolean exists = repository.checkEmailExists("user1@example.com");

        // Then
        assertTrue(exists);
    }

    @Test
    void testCheckEmailExists_NonExistentEmail() {
        // When
        boolean exists = repository.checkEmailExists("nonexistent@example.com");

        // Then
        assertFalse(exists);
    }

    @Test
    void testCheckEmailExists_CaseSensitive() {
        // When
        boolean exists = repository.checkEmailExists("USER1@EXAMPLE.COM");

        // Then
        assertFalse(exists);
    }

    @Test
    void testIdGeneration() {
        // Given
        UserEntity userA = UserEntity.builder()
                .login("userA")
                .passwordHash("passwordA")
                .emailAddress("userA@example.com")
                .build();

        UserEntity userB = UserEntity.builder()
                .login("userB")
                .passwordHash("passwordB")
                .emailAddress("userB@example.com")
                .build();

        // When
        UserEntity resultA = repository.store(userA);
        UserEntity resultB = repository.store(userB);

        // Then
        assertNotNull(resultA.getId());
        assertNotNull(resultB.getId());
        assertTrue(resultB.getId() > resultA.getId());
    }

    @Test
    void testMultipleUsers() {
        // Given
        UserEntity user3 = UserEntity.builder()
                .login("user3")
                .passwordHash("password789")
                .emailAddress("user3@example.com")
                .build();

        // When
        repository.store(user3);

        // Then
        assertTrue(repository.checkLoginExists("user1"));
        assertTrue(repository.checkLoginExists("user2"));
        assertTrue(repository.checkLoginExists("user3"));

        assertTrue(repository.checkEmailExists("user1@example.com"));
        assertTrue(repository.checkEmailExists("user2@example.com"));
        assertTrue(repository.checkEmailExists("user3@example.com"));
    }

    @Test
    void testUserWithSpecialCharacters() {
        // Given
        UserEntity specialUser = UserEntity.builder()
                .login("user.name+tag")
                .passwordHash("password!@#$%")
                .emailAddress("user.name+tag@example-domain.co.uk")
                .build();

        // When
        UserEntity result = repository.store(specialUser);

        // Then
        assertEquals("user.name+tag", result.getLogin());
        assertEquals("password!@#$%", result.getPasswordHash());
        assertEquals("user.name+tag@example-domain.co.uk", result.getEmailAddress());

        // Verify it can be found
        assertTrue(repository.checkLoginExists("user.name+tag"));
        assertTrue(repository.checkEmailExists("user.name+tag@example-domain.co.uk"));
    }
}
