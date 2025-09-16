package spbstu.TasksApplication.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spbstu.TasksApplication.exception.ResourceNotFoundException;
import spbstu.TasksApplication.model.User;
import spbstu.TasksApplication.repository.impl.InMemoryUserRepository;
import spbstu.TasksApplication.service.UserService;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private UserService userService;
    private User testUser;

    @BeforeEach
    void setUp() {
        userService = new UserService(new InMemoryUserRepository());
        testUser = User.builder()
                .username("testuser")
                .password("password123")
                .email("test@example.com")
                .build();
    }

    @Test
    void registerUser_ShouldRegisterNewUser() {
        User registeredUser = userService.registerUser(testUser);
        
        assertNotNull(registeredUser.getUserId());
        assertEquals(testUser.getUsername(), registeredUser.getUsername());
        assertEquals(testUser.getEmail(), registeredUser.getEmail());
    }

    @Test
    void registerUser_ShouldThrowException_WhenUsernameIsEmpty() {
        testUser.setUsername("");
        
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(testUser));
    }

    @Test
    void registerUser_ShouldThrowException_WhenUsernameIsTooShort() {
        testUser.setUsername("ab");
        
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(testUser));
    }

    @Test
    void registerUser_ShouldThrowException_WhenPasswordIsEmpty() {
        testUser.setPassword("");
        
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(testUser));
    }

    @Test
    void registerUser_ShouldThrowException_WhenPasswordIsTooShort() {
        testUser.setPassword("12345");
        
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(testUser));
    }

    @Test
    void registerUser_ShouldThrowException_WhenEmailIsInvalid() {
        testUser.setEmail("invalid-email");
        
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(testUser));
    }

    @Test
    void registerUser_ShouldThrowException_WhenUsernameAlreadyExists() {
        userService.registerUser(testUser);
        
        User duplicateUser = User.builder()
                .username("testuser")
                .password("password456")
                .email("different@example.com")
                .build();
        
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(duplicateUser));
    }

    @Test
    void registerUser_ShouldThrowException_WhenEmailAlreadyExists() {
        userService.registerUser(testUser);
        
        User duplicateUser = User.builder()
                .username("differentuser")
                .password("password456")
                .email("test@example.com")
                .build();
        
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(duplicateUser));
    }

    @Test
    void login_ShouldLoginUser_WhenValidCredentials() {
        userService.registerUser(testUser);
        
        User loggedInUser = userService.login("testuser", "password123");
        
        assertEquals(testUser.getUsername(), loggedInUser.getUsername());
        assertEquals(testUser.getEmail(), loggedInUser.getEmail());
    }

    @Test
    void login_ShouldThrowException_WhenInvalidUsername() {
        userService.registerUser(testUser);
        
        assertThrows(ResourceNotFoundException.class, () -> userService.login("invaliduser", "password123"));
    }

    @Test
    void login_ShouldThrowException_WhenInvalidPassword() {
        userService.registerUser(testUser);
        
        assertThrows(ResourceNotFoundException.class, () -> userService.login("testuser", "wrongpassword"));
    }
}
