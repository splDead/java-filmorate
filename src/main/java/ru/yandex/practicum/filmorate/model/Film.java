package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * Класс, описывающий модель фильма.
 */
@Data
public class Film {

    /** Максимально допустимая длина описания фильма. */
    private static final int MAX_DESCRIPTION_LENGTH = 200;

    /** Уникальный идентификатор фильма. */
    @EqualsAndHashCode.Exclude
    private Long id;

    /** Название фильма. */
    @NotBlank
    private String name;

    /** Описание фильма (не более 200 символов). */
    @Size(max = MAX_DESCRIPTION_LENGTH)
    private String description;

    /** Дата релиза фильма. */
    @NotNull
    private LocalDate releaseDate;

    /** Продолжительность фильма в минутах. */
    @NotNull
    @Positive
    private Integer duration;
}
