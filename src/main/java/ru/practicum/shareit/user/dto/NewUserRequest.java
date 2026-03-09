package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewUserRequest {
    private Long id;
    @NotBlank(message = "Имя пользователя должно быть указано")
    private String name;
    @NotBlank(message = "Email пользователя должен быть указан")
    @Email(message = "Email должен быть в формате user@mail.ru")
    private String email;
}
