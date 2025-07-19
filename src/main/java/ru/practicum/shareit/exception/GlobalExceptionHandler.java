package ru.practicum.shareit.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({NotFoundException.class, UserNotFoundException.class, ItemNotFoundException.class})
    public ResponseEntity<Map<String, String>> catchNotFoundException(RuntimeException e) {
        log.warn("404 - Ресурс не найден: {}", e.getMessage());
        return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        log.warn("409 - Конфликт данных (вероятно, дубликат email): {}", e.getMessage());
        return new ResponseEntity<>(
                Map.of("error", "Пользователь с таким email уже существует."),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler({ValidationException.class, UnavailableItemException.class, BookingException.class, CommentException.class})
    public ResponseEntity<Map<String, String>> catchBadRequestExceptions(RuntimeException e) {
        log.warn("400 - Некорректный запрос: {}", e.getMessage());
        return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        log.warn("400 - Ошибка валидации аргумента: {}", errorMessage);
        return new ResponseEntity<>(Map.of("error", errorMessage), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining(", "));
        log.warn("400 - Нарушение ограничений: {}", message);
        return new ResponseEntity<>(Map.of("error", message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException e) {
        log.warn("403 - Доступ запрещен: {}", e.getMessage());
        return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Map<String, String>> handleUnexpectedException(final Throwable e) {
        log.error("500 - Произошла непредвиденная ошибка: ", e);
        return new ResponseEntity<>(Map.of("error", "Произошла внутренняя ошибка сервера."), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}