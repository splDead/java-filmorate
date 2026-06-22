package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * Класс, описывающий модель пользователя.
 */
@Data
public class User {

    /** Уникальный идентификатор пользователя. */
    @EqualsAndHashCode.Exclude
    private Long id;

    /** Электронная почта пользователя. */
    @Email
    @NotBlank
    private String email;

    /** Логин пользователя в системе. */
    @NotBlank
    private String login;

    /** Имя пользователя для отображения. */
    private String name;

    /** Дата рождения пользователя. */
    @NotNull
    @PastOrPresent
    private LocalDate birthday;
}
