package spbstu.TasksApplication.repository.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spbstu.TasksApplication.model.User;
import spbstu.TasksApplication.repository.UserRepository;

import java.util.Optional;

@Repository
@Profile("h2")
public interface JpaUserRepository extends JpaRepository<User, Long>, UserRepository {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
