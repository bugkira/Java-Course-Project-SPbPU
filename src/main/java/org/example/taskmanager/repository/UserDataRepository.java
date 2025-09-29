package org.example.taskmanager.repository;

import java.util.Optional;

import org.example.taskmanager.domain.UserEntity;

public interface UserDataRepository {
    UserEntity save(UserEntity user);
    Optional<UserEntity> findByLogin(String login);
    Optional<UserEntity> findByEmailAddress(String emailAddress);
    boolean existsByLogin(String login);
    boolean existsByEmailAddress(String emailAddress);
    boolean existsById(Long id);
}
