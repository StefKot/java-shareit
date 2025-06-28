package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class UserRepositoryImpl implements UserRepository {
    private final List<User> users = new ArrayList<>();

    @Override
    public List<User> findAll() {
        return users;
    }

    @Override
    public User getUser(Long id) {
        return users.stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public User save(User user) {
        user.setId(getId());
        users.add(user);
        return user;
    }

    @Override
    public void deleteUser(Long userId) {
        users.removeIf(u -> u.getId().equals(userId));
    }

    @Override
    public Optional<User> update(User user, Long userId) {
        return users.stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .map(foundUser -> {
                    foundUser.setEmail(user.getEmail());
                    foundUser.setName(user.getName());
                    return foundUser;
                });
    }

    @Override
    public Optional<User> update(User user, String config, Long userId) {
        return users.stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .map(foundUser -> {
                    switch (config.toLowerCase()) {
                        case "email":
                            foundUser.setEmail(user.getEmail());
                            break;
                        case "name":
                            foundUser.setName(user.getName());
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown config: " + config);
                    }
                    return foundUser;
                });
    }


    private long getId() {
        long lastId = users.stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0);
        return lastId + 1;
    }
}