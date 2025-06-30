package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository {
    List<User> findAll();

    User findById(Long id);

    User save(User user);

    void deleteById(Long userId);

    User findByEmail(String email);
}