package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.ValidReleaseDate;

import java.time.LocalDate;
import java.util.LinkedHashSet;

/**
 * Объект переноса данных (DTO) для сущности фильма.
 */
@Data
@Builder
public class FilmDto {

    /** Максимально допустимая длина описания фильма. */
    public static final int MAX_DESCRIPTION_LENGTH = 200;

    /** Уникальный идентификатор фильма. */
    private Long id;

    /** Название фильма. */
    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    /** Описание фильма. */
    @Size(max = MAX_DESCRIPTION_LENGTH,
            message = "Максимальная длина описания — 200 символов")
    private String description;

    /** Дата релиза фильма. */
    @NotNull(message = "Дата релиза обязательна")
    @ValidReleaseDate
    private LocalDate releaseDate;

    /** Продолжительность фильма в минутах. */
    @NotNull(message = "Продолжительность фильма обязательна")
    @Positive(message = "Продолжительность фильма должна быть положительной")
    private Integer duration;

    /** Рейтинг MPA фильма. */
    private MpaDto mpa;

    /** Список жанров, к которым относится фильм. */
    @Builder.Default
    private LinkedHashSet<GenreDto> genres = new LinkedHashSet<>();
}
