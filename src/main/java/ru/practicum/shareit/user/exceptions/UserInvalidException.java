package ru.practicum.shareit.user.exceptions;

import ru.practicum.shareit.user.User;

public class UserInvalidException extends RuntimeException {
    User user;

    public UserInvalidException(String message, User user) {
        super(message);
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}