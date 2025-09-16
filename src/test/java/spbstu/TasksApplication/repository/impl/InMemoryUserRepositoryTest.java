package spbstu.TasksApplication.repository.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spbstu.TasksApplication.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserRepositoryTest {
    private InMemoryUserRepository userRepository;
    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository = new InMemoryUserRepository();
        testUser = User.builder()
                .username("testuser")
                .password("password123")
                .email("test@example.com")
                .build();
    }

    @Test
    void save_ShouldCreateNewUser_WhenUserIdIsNull() {
        User savedUser = userRepository.save(testUser);
        
        assertNotNull(savedUser.getUserId());
        assertEquals(testUser.getUsername(), savedUser.getUsername());
        assertEquals(testUser.getEmail(), savedUser.getEmail());
    }

    @Test
    void save_ShouldUpdateExistingUser_WhenUserIdIsNotNull() {
        User savedUser = userRepository.save(testUser);
        savedUser.setUsername("updateduser");
        
        User updatedUser = userRepository.save(savedUser);
        
        assertEquals(savedUser.getUserId(), updatedUser.getUserId());
        assertEquals("updateduser", updatedUser.getUsername());
    }

    @Test
    void findByUsername_ShouldReturnUser_WhenExists() {
        userRepository.save(testUser);
        Optional<User> foundUser = userRepository.findByUsername("testuser");
        
        assertTrue(foundUser.isPresent());
        assertEquals(testUser.getUsername(), foundUser.get().getUsername());
    }

    @Test
    void findByUsername_ShouldReturnEmpty_WhenNotExists() {
        Optional<User> foundUser = userRepository.findByUsername("nonexistent");
        
        assertTrue(foundUser.isEmpty());
    }

    @Test
    void existsByUsername_ShouldReturnTrue_WhenUserExists() {
        userRepository.save(testUser);
        
        assertTrue(userRepository.existsByUsername("testuser"));
    }

    @Test
    void existsByUsername_ShouldReturnFalse_WhenUserDoesNotExist() {
        assertFalse(userRepository.existsByUsername("nonexistent"));
    }

    @Test
    void existsByEmail_ShouldReturnTrue_WhenEmailExists() {
        userRepository.save(testUser);
        
        assertTrue(userRepository.existsByEmail("test@example.com"));
    }

    @Test
    void existsByEmail_ShouldReturnFalse_WhenEmailDoesNotExist() {
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
    }

    @Test
    void multipleUsers_ShouldBeStoredSeparately() {
        User user1 = userRepository.save(testUser);
        
        User user2 = User.builder()
                .username("anotheruser")
                .password("password456")
                .email("another@example.com")
                .build();
        User user2Saved = userRepository.save(user2);
        
        assertNotEquals(user1.getUserId(), user2Saved.getUserId());
        assertTrue(userRepository.existsByUsername("testuser"));
        assertTrue(userRepository.existsByUsername("anotheruser"));
        assertTrue(userRepository.existsByEmail("test@example.com"));
        assertTrue(userRepository.existsByEmail("another@example.com"));
    }
}
