package org.example.taskmanager.repository.memory;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.example.taskmanager.domain.UserEntity;
import org.example.taskmanager.repository.UserDataRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Profile("inmemory")
public class MemoryUserRepository implements UserDataRepository {
    private final Map<Long, UserEntity> storage = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public UserEntity store(UserEntity user) {
        if (user.getId() == null) {
            user.setId(idGenerator.getAndIncrement());
        }
        storage.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<UserEntity> findByLogin(String login) {
        return storage.values().stream()
                .filter(user -> user.getLogin().equals(login))
                .findFirst();
    }

    @Override
    public boolean checkLoginExists(String login) {
        return storage.values().stream()
                .anyMatch(user -> user.getLogin().equals(login));
    }

    @Override
    public boolean checkEmailExists(String email) {
        return storage.values().stream()
                .anyMatch(user -> user.getEmailAddress().equals(email));
    }
}
