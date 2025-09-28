package org.example.taskmanager.repository.jpa;

import java.util.Optional;

import org.example.taskmanager.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    
    /**
     * Find user by login (for authentication)
     */
    Optional<UserEntity> findByLogin(String login);
    
    /**
     * Find user by email address
     */
    Optional<UserEntity> findByEmailAddress(String emailAddress);
    
    /**
     * Check if user exists by login
     */
    boolean existsByLogin(String login);
    
    /**
     * Check if user exists by email address
     */
    boolean existsByEmailAddress(String emailAddress);
}
