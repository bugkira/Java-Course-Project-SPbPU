package org.example.taskmanager.service;

import org.example.taskmanager.domain.UserEntity;
import org.example.taskmanager.exception.EntityNotFoundException;
import org.example.taskmanager.repository.UserDataRepository;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserManagementServiceTest {

    @Mock
    private UserDataRepository userRepository;

    @InjectMocks
    private UserManagementService userService;

    private UserEntity validUser;
    private UserEntity existingUser;

    @BeforeEach
    void setUp() {
        validUser = UserEntity.builder()
                .id(1L)
                .login("testuser")
                .passwordHash("password123")
                .emailAddress("test@example.com")
                .build();

        existingUser = UserEntity.builder()
                .id(2L)
                .login("existinguser")
                .passwordHash("password123")
                .emailAddress("existing@example.com")
                .build();
    }

    @Test
    void testCreateUserAccount_Success() {
        // Given
        when(userRepository.existsByLogin("testuser")).thenReturn(false);
        when(userRepository.existsByEmailAddress("test@example.com")).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenReturn(validUser);

        // When
        UserEntity result = userService.createUserAccount(validUser);

        // Then
        assertEquals(validUser, result);
        verify(userRepository).existsByLogin("testuser");
        verify(userRepository).existsByEmailAddress("test@example.com");
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void testCreateUserAccount_ValidationError_EmptyLogin() {
        // Given
        UserEntity invalidUser = UserEntity.builder()
                .login("")
                .passwordHash("password123")
                .emailAddress("test@example.com")
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> userService.createUserAccount(invalidUser));
        
        assertEquals("Login must not be empty", exception.getMessage());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testCreateUserAccount_ValidationError_ShortLogin() {
        // Given
        UserEntity invalidUser = UserEntity.builder()
                .login("ab")
                .passwordHash("password123")
                .emailAddress("test@example.com")
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> userService.createUserAccount(invalidUser));
        
        assertEquals("Login must be at least 3 characters long", exception.getMessage());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testCreateUserAccount_ValidationError_EmptyPassword() {
        // Given
        UserEntity invalidUser = UserEntity.builder()
                .login("testuser")
                .passwordHash("")
                .emailAddress("test@example.com")
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> userService.createUserAccount(invalidUser));
        
        assertEquals("Password must not be empty", exception.getMessage());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testCreateUserAccount_ValidationError_ShortPassword() {
        // Given
        UserEntity invalidUser = UserEntity.builder()
                .login("testuser")
                .passwordHash("12345")
                .emailAddress("test@example.com")
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> userService.createUserAccount(invalidUser));
        
        assertEquals("Password must be at least 6 characters long", exception.getMessage());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testCreateUserAccount_ValidationError_EmptyEmail() {
        // Given
        UserEntity invalidUser = UserEntity.builder()
                .login("testuser")
                .passwordHash("password123")
                .emailAddress("")
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> userService.createUserAccount(invalidUser));
        
        assertEquals("Email must not be empty", exception.getMessage());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testCreateUserAccount_ValidationError_InvalidEmail() {
        // Given
        UserEntity invalidUser = UserEntity.builder()
                .login("testuser")
                .passwordHash("password123")
                .emailAddress("invalid-email")
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> userService.createUserAccount(invalidUser));
        
        assertEquals("Invalid email format", exception.getMessage());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testCreateUserAccount_DuplicateLogin() {
        // Given
        when(userRepository.existsByLogin("testuser")).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> userService.createUserAccount(validUser));
        
        assertEquals("Login already exists", exception.getMessage());
        verify(userRepository).existsByLogin("testuser");
        verify(userRepository, never()).existsByEmailAddress(anyString());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testCreateUserAccount_DuplicateEmail() {
        // Given
        when(userRepository.existsByLogin("testuser")).thenReturn(false);
        when(userRepository.existsByEmailAddress("test@example.com")).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> userService.createUserAccount(validUser));
        
        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository).existsByLogin("testuser");
        verify(userRepository).existsByEmailAddress("test@example.com");
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testAuthenticateUser_Success() {
        // Given
        when(userRepository.findByLogin("testuser")).thenReturn(java.util.Optional.of(validUser));

        // When
        UserEntity result = userService.authenticateUser("testuser", "password123");

        // Then
        assertEquals(validUser, result);
        verify(userRepository).findByLogin("testuser");
    }

    @Test
    void testAuthenticateUser_InvalidLogin() {
        // Given
        when(userRepository.findByLogin("nonexistent")).thenReturn(java.util.Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
            () -> userService.authenticateUser("nonexistent", "password123"));
        
        assertEquals("Invalid login or password", exception.getMessage());
        verify(userRepository).findByLogin("nonexistent");
    }

    @Test
    void testAuthenticateUser_InvalidPassword() {
        // Given
        when(userRepository.findByLogin("testuser")).thenReturn(java.util.Optional.of(validUser));

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
            () -> userService.authenticateUser("testuser", "wrongpassword"));
        
        assertEquals("Invalid login or password", exception.getMessage());
        verify(userRepository).findByLogin("testuser");
    }

    @Test
    void testPerformUserValidation_ValidUser() {
        // Given
        UserEntity validUser = UserEntity.builder()
                .login("validuser")
                .passwordHash("validpass123")
                .emailAddress("valid@example.com")
                .build();

        when(userRepository.existsByLogin("validuser")).thenReturn(false);
        when(userRepository.existsByEmailAddress("valid@example.com")).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenReturn(validUser);

        // When
        UserEntity result = userService.createUserAccount(validUser);

        // Then
        assertNotNull(result);
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void testEmailValidationPattern_ValidEmails() {
        // Given
        String[] validEmails = {
            "test@example.com",
            "user.name@domain.co.uk",
            "user+tag@example.org",
            "user123@test-domain.com"
        };

        for (String email : validEmails) {
            UserEntity user = UserEntity.builder()
                    .login("testuser")
                    .passwordHash("password123")
                    .emailAddress(email)
                    .build();

            when(userRepository.existsByLogin("testuser")).thenReturn(false);
            when(userRepository.existsByEmailAddress(email)).thenReturn(false);
            when(userRepository.save(any(UserEntity.class))).thenReturn(user);

            // When & Then
            assertDoesNotThrow(() -> userService.createUserAccount(user));
        }
    }

    @Test
    void testEmailValidationPattern_InvalidEmails() {
        // Given
        String invalidEmail = "invalid-email";
        UserEntity user = UserEntity.builder()
                .login("testuser")
                .passwordHash("password123")
                .emailAddress(invalidEmail)
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> userService.createUserAccount(user));
        
        // Check that some validation error occurred
        assertNotNull(exception.getMessage());
        assertFalse(exception.getMessage().isEmpty());
    }
}
