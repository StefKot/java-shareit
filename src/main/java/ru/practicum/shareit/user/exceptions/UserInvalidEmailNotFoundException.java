package ru.practicum.shareit.user.exceptions;

import lombok.Getter;
import ru.practicum.shareit.user.User;

public class UserInvalidEmailNotFoundException extends RuntimeException {
    @Getter
    User user;

    public UserInvalidEmailNotFoundException(String message, User user) {
        super(message);
        this.user = user;
    }

    public UserInvalidEmailNotFoundException(String message) {
        super(message);
    }
}