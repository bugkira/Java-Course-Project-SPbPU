package org.example.taskmanager.repository.jpa.impl;

import java.util.Optional;

import org.example.taskmanager.domain.UserEntity;
import org.example.taskmanager.repository.UserDataRepository;
import org.example.taskmanager.repository.jpa.UserJpaRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("h2")
public class JpaUserRepository implements UserDataRepository {
    
    private final UserJpaRepository jpaRepository;
    
    public JpaUserRepository(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public Optional<UserEntity> findByLogin(String login) {
        return jpaRepository.findByLogin(login);
    }
    
    @Override
    public Optional<UserEntity> findByEmailAddress(String emailAddress) {
        return jpaRepository.findByEmailAddress(emailAddress);
    }
    
    @Override
    public boolean existsByLogin(String login) {
        return jpaRepository.existsByLogin(login);
    }
    
    @Override
    public boolean existsByEmailAddress(String emailAddress) {
        return jpaRepository.existsByEmailAddress(emailAddress);
    }
    
    @Override
    public UserEntity save(UserEntity user) {
        return jpaRepository.save(user);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }
}
