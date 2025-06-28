package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository {
    List<User> findAll();

    User getUser(Long id);

    User save(User user);

    void deleteUser(Long userId);

    Optional<User> update(User user, Long userId);

    Optional<User> update(User user, String config, Long userId);
}