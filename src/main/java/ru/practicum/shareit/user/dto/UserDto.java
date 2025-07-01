package ru.practicum.shareit.user.dto;

import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
public class UserDto {
    private Long id;

    @NotBlank(message = "Email не может быть пустым.")
    @Email(message = "Некорректный формат email.")
    private String email;

    private String name;

    public UserDto(Long id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }
}