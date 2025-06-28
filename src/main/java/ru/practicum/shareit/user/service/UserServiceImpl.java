package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.exceptions.UserInvalidEmailNotFoundException;
import ru.practicum.shareit.user.exceptions.UserInvalidException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    @Override
    public User getUser(Long userId) {
        if (userId == null || userId <= 0 || repository.findAll().getLast().getId() < userId || repository.findAll().stream().noneMatch(u -> u.getId().equals(userId))) {
            throw new UserInvalidEmailNotFoundException("User with id = " + userId + " not found");
        }
        return repository.getUser(userId);
    }

    @Override
    public void deleteUser(Long userId) {
        if (userId == null || userId <= 0 || repository.findAll().getLast().getId() < userId) {
            throw new UserInvalidEmailNotFoundException("User with id = " + userId + " not found");
        }
        repository.deleteUser(userId);
    }

    @Override
    public User saveUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new UserInvalidEmailNotFoundException("Email is blank", user);
        }
        if (!user.getEmail().contains("@")) {
            throw new UserInvalidEmailNotFoundException("Email is not valid", user);
        }
        if (repository.findAll().stream()
                .anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            throw new UserInvalidException("Email already exists", user);
        }
        return repository.save(user);
    }

    @Override
    public User update(User user, Long userId) {
        if (userId == null || userId <= 0 || repository.findAll().getLast().getId() < userId
                || repository.findAll().stream().noneMatch(u -> u.getId().equals(userId))) {
            throw new UserInvalidEmailNotFoundException("User with id = " + userId + " not found");
        }
        if (user.getEmail() == null && user.getName() == null) {
            throw new UserInvalidEmailNotFoundException("Email or name is blank", user);
        }
        if (user.getEmail() != null && user.getName() != null && user.getEmail().contains("@")) {
            if (repository.findAll().stream()
                    .anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
                throw new UserInvalidException("Email already exists", user);
            }
            return repository.update(user, userId).isPresent() ? repository.update(user, userId).get() : null;
        }
        if (user.getEmail() != null && user.getName() == null && user.getEmail().contains("@")) {
            if (repository.findAll().stream()
                    .anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
                throw new UserInvalidException("Email already exists", user);
            }
            return repository.update(user, "email", userId).isPresent() ? repository.update(user, "email", userId).get() : null;

        }
        if (user.getEmail() == null && user.getName() != null) {
            return repository.update(user, "name", userId).isPresent() ? repository.update(user, "name", userId).get() : null;
        }
        return null;
    }
}