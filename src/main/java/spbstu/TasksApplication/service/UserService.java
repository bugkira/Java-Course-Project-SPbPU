package spbstu.TasksApplication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spbstu.TasksApplication.exception.ResourceNotFoundException;
import spbstu.TasksApplication.model.User;
import spbstu.TasksApplication.repository.UserRepository;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    private final UserRepository userRepository;

    public User registerUser(User user) {
        validateUser(user);
        checkUsernameExists(user.getUsername());
        checkEmailExists(user.getEmail());

        return userRepository.save(user);
    }

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid username or password"));
        if (!user.getPassword().equals(password)) {
            throw new ResourceNotFoundException("Invalid username or password");
        }
        return user;
    }

    private void validateUser(User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (user.getUsername().length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters long");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (user.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    private void checkUsernameExists(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
    }

    private void checkEmailExists(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
    }
}
