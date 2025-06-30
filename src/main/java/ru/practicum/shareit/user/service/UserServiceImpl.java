package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.UserEmailAlreadyExistsException;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<UserDto> getAllUsers() {
        return repository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = findUserByIdOrThrow(userId);
        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(Long userId) {
        findUserByIdOrThrow(userId);
        repository.deleteById(userId);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (repository.findByEmail(userDto.getEmail()) != null) {
            throw new UserEmailAlreadyExistsException("Email " + userDto.getEmail() + " already exists.");
        }
        User user = UserMapper.toUser(userDto);
        User savedUser = repository.save(user);
        return UserMapper.toUserDto(savedUser);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User existingUser = findUserByIdOrThrow(userId);

        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            User userWithSameEmail = repository.findByEmail(userDto.getEmail());
            if (userWithSameEmail != null && !Objects.equals(userWithSameEmail.getId(), userId)) {
                throw new UserEmailAlreadyExistsException("Email " + userDto.getEmail() + " already exists.");
            }
            existingUser.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            existingUser.setName(userDto.getName());
        }

        User updatedUser = repository.save(existingUser);
        return UserMapper.toUserDto(updatedUser);
    }

    private User findUserByIdOrThrow(Long userId) {
        User user = repository.findById(userId);
        if (user == null) {
            throw new UserNotFoundException("User with id = " + userId + " not found");
        }
        return user;
    }
}