package ru.practicum.shareit.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.user.User;

import java.util.Map;

@RestControllerAdvice
public class UserValidationHandler {

    @ExceptionHandler(UserEmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleEmailConflict(UserEmailAlreadyExistsException ex) {
        Map<String, String> error = Map.of("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserInvalidException.class)
    public ResponseEntity<Map<String, User>> handleEmailAlreadyExists(UserInvalidException ex) {
        Map<String, User> error = Map.of(ex.getMessage(), ex.getUser());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserInvalidEmailNotFoundException.class)
    public ResponseEntity<Map<String, User>> handleEmailNotFound(UserInvalidEmailNotFoundException ex) {
        Map<String, User> error = Map.of(ex.getMessage(), ex.getUser());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFound(UserNotFoundException ex) {
        Map<String, String> error = Map.of("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}