package org.example.taskmanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.example.taskmanager.exception.EntityNotFoundException;
import org.example.taskmanager.domain.UserEntity;
import org.example.taskmanager.repository.UserDataRepository;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserManagementService {
    private static final Pattern EMAIL_VALIDATION_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    private final UserDataRepository userRepository;

    public UserEntity createUserAccount(UserEntity user) {
        performUserValidation(user);
        verifyLoginUniqueness(user.getLogin());
        verifyEmailUniqueness(user.getEmailAddress());

        return userRepository.store(user);
    }

    public UserEntity authenticateUser(String login, String password) {
        UserEntity user = userRepository.findByLogin(login)
                .orElseThrow(() -> new EntityNotFoundException("Invalid login or password"));
        
        if (!user.getPasswordHash().equals(password)) {
            throw new EntityNotFoundException("Invalid login or password");
        }
        return user;
    }

    private void performUserValidation(UserEntity user) {
        if (user.getLogin() == null || user.getLogin().trim().isEmpty()) {
            throw new IllegalArgumentException("Login must not be empty");
        }
        if (user.getLogin().length() < 3) {
            throw new IllegalArgumentException("Login must be at least 3 characters long");
        }
        if (user.getPasswordHash() == null || user.getPasswordHash().trim().isEmpty()) {
            throw new IllegalArgumentException("Password must not be empty");
        }
        if (user.getPasswordHash().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
        if (user.getEmailAddress() == null || user.getEmailAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("Email must not be empty");
        }
        if (!EMAIL_VALIDATION_PATTERN.matcher(user.getEmailAddress()).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    private void verifyLoginUniqueness(String login) {
        if (userRepository.checkLoginExists(login)) {
            throw new IllegalArgumentException("Login already exists");
        }
    }

    private void verifyEmailUniqueness(String email) {
        if (userRepository.checkEmailExists(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
    }
}
