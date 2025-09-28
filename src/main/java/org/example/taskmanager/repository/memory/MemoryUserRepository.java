package org.example.taskmanager.repository.memory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.example.taskmanager.domain.UserEntity;
import org.example.taskmanager.repository.UserDataRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("inmemory")
public class MemoryUserRepository implements UserDataRepository {
    private final Map<Long, UserEntity> storage = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public UserEntity save(UserEntity user) {
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
    public Optional<UserEntity> findByEmailAddress(String emailAddress) {
        return storage.values().stream()
                .filter(user -> user.getEmailAddress().equals(emailAddress))
                .findFirst();
    }

    @Override
    public boolean existsByLogin(String login) {
        return storage.values().stream()
                .anyMatch(user -> user.getLogin().equals(login));
    }

    @Override
    public boolean existsByEmailAddress(String emailAddress) {
        return storage.values().stream()
                .anyMatch(user -> user.getEmailAddress().equals(emailAddress));
    }
}
