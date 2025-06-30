package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto createUser(UserDto userDto);

    void deleteUser(Long userId);

    UserDto updateUser(Long userId, UserDto userDto);

    UserDto getUserById(Long userId);
}