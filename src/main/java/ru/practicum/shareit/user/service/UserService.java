package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User saveUser(User user);

    void deleteUser(Long userId);

    User update(User user, Long userId);

    User getUser(Long userId);
}