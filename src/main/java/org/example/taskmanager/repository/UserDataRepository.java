package org.example.taskmanager.repository;

import java.util.Optional;

import org.example.taskmanager.domain.UserEntity;

public interface UserDataRepository {
    UserEntity store(UserEntity user);
    Optional<UserEntity> findByLogin(String login);
    boolean checkLoginExists(String login);
    boolean checkEmailExists(String email);
}
