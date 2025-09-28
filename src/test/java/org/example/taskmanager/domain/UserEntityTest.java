package org.example.taskmanager.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class UserEntityTest {

    @Test
    void testBuilder_Success() {
        // When
        UserEntity user = UserEntity.builder()
                .login("testuser")
                .passwordHash("password123")
                .emailAddress("test@example.com")
                .build();

        // Then
        assertNotNull(user);
        assertEquals("testuser", user.getLogin());
        assertEquals("password123", user.getPasswordHash());
        assertEquals("test@example.com", user.getEmailAddress());
    }

    @Test
    void testBuilder_WithAllFields() {
        // Given
        Long id = 1L;
        String login = "completeuser";
        String passwordHash = "completepass123";
        String emailAddress = "complete@example.com";

        // When
        UserEntity user = UserEntity.builder()
                .id(id)
                .login(login)
                .passwordHash(passwordHash)
                .emailAddress(emailAddress)
                .build();

        // Then
        assertEquals(id, user.getId());
        assertEquals(login, user.getLogin());
        assertEquals(passwordHash, user.getPasswordHash());
        assertEquals(emailAddress, user.getEmailAddress());
    }

    @Test
    void testEqualsAndHashCode_SameObjects() {
        // Given
        UserEntity user1 = UserEntity.builder()
                .id(1L)
                .login("testuser")
                .passwordHash("password123")
                .emailAddress("test@example.com")
                .build();

        UserEntity user2 = UserEntity.builder()
                .id(1L)
                .login("testuser")
                .passwordHash("password123")
                .emailAddress("test@example.com")
                .build();

        // When & Then
        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testEqualsAndHashCode_DifferentObjects() {
        // Given
        UserEntity user1 = UserEntity.builder()
                .id(1L)
                .login("user1")
                .passwordHash("password123")
                .emailAddress("user1@example.com")
                .build();

        UserEntity user2 = UserEntity.builder()
                .id(2L)
                .login("user2")
                .passwordHash("password456")
                .emailAddress("user2@example.com")
                .build();

        // When & Then
        assertNotEquals(user1, user2);
        assertNotEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testEqualsAndHashCode_DifferentLogins() {
        // Given
        UserEntity user1 = UserEntity.builder()
                .id(1L)
                .login("user1")
                .passwordHash("samepassword")
                .emailAddress("same@example.com")
                .build();

        UserEntity user2 = UserEntity.builder()
                .id(1L)
                .login("user2")
                .passwordHash("samepassword")
                .emailAddress("same@example.com")
                .build();

        // When & Then
        assertNotEquals(user1, user2);
        assertNotEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testEqualsAndHashCode_DifferentEmails() {
        // Given
        UserEntity user1 = UserEntity.builder()
                .id(1L)
                .login("sameuser")
                .passwordHash("samepassword")
                .emailAddress("user1@example.com")
                .build();

        UserEntity user2 = UserEntity.builder()
                .id(1L)
                .login("sameuser")
                .passwordHash("samepassword")
                .emailAddress("user2@example.com")
                .build();

        // When & Then
        assertNotEquals(user1, user2);
        assertNotEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testToString() {
        // Given
        UserEntity user = UserEntity.builder()
                .id(1L)
                .login("testuser")
                .passwordHash("password123")
                .emailAddress("test@example.com")
                .build();

        // When
        String toString = user.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("UserEntity"));
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("login=testuser"));
        assertTrue(toString.contains("passwordHash=password123"));
        assertTrue(toString.contains("emailAddress=test@example.com"));
    }

    @Test
    void testSetters() {
        // Given
        UserEntity user = UserEntity.builder()
                .login("originaluser")
                .passwordHash("originalpass")
                .emailAddress("original@example.com")
                .build();

        // When
        user.setId(999L);
        user.setLogin("updateduser");
        user.setPasswordHash("updatedpass");
        user.setEmailAddress("updated@example.com");

        // Then
        assertEquals(999L, user.getId());
        assertEquals("updateduser", user.getLogin());
        assertEquals("updatedpass", user.getPasswordHash());
        assertEquals("updated@example.com", user.getEmailAddress());
    }

    @Test
    void testNoArgsConstructor() {
        // When
        UserEntity user = new UserEntity();

        // Then
        assertNotNull(user);
        assertNull(user.getId());
        assertNull(user.getLogin());
        assertNull(user.getPasswordHash());
        assertNull(user.getEmailAddress());
    }

    @Test
    void testAllArgsConstructor() {
        // Given
        Long id = 1L;
        String login = "testuser";
        String passwordHash = "password123";
        String emailAddress = "test@example.com";

        // When
        UserEntity user = new UserEntity(id, login, passwordHash, emailAddress);

        // Then
        assertEquals(id, user.getId());
        assertEquals(login, user.getLogin());
        assertEquals(passwordHash, user.getPasswordHash());
        assertEquals(emailAddress, user.getEmailAddress());
    }

    @Test
    void testUserWithSpecialCharacters() {
        // Given
        String specialLogin = "user.name+tag";
        String specialPassword = "password!@#$%^&*()";
        String specialEmail = "user.name+tag@example-domain.co.uk";

        // When
        UserEntity user = UserEntity.builder()
                .login(specialLogin)
                .passwordHash(specialPassword)
                .emailAddress(specialEmail)
                .build();

        // Then
        assertEquals(specialLogin, user.getLogin());
        assertEquals(specialPassword, user.getPasswordHash());
        assertEquals(specialEmail, user.getEmailAddress());
    }

    @Test
    void testUserWithLongValues() {
        // Given
        String longLogin = "verylongusername123456789";
        String longPassword = "verylongpassword12345678901234567890";
        String longEmail = "verylongemailaddress123456789@verylongdomainname123456789.com";

        // When
        UserEntity user = UserEntity.builder()
                .login(longLogin)
                .passwordHash(longPassword)
                .emailAddress(longEmail)
                .build();

        // Then
        assertEquals(longLogin, user.getLogin());
        assertEquals(longPassword, user.getPasswordHash());
        assertEquals(longEmail, user.getEmailAddress());
    }
}
