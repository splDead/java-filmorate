package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * Объект переноса данных (DTO) для сущности пользователя.
 */
@Data
@Builder
public class UserDto {

    /** Уникальный идентификатор пользователя. */
    private Long id;

    /** Электронная почта пользователя. */
    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Некорректный формат электронной почты")
    private String email;

    /** Логин пользователя. */
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "^\\S+$",
            message = "Логин не может содержать пробелы")
    private String login;

    /** Имя пользователя для отображения. */
    private String name;

    /** Дата рождения пользователя. */
    @NotNull(message = "Дата рождения обязательна")
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}
