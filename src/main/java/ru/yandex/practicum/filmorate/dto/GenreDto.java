package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Объект переноса данных (DTO) для сущности жанра фильма.
 */
@Data
@Builder
public class GenreDto {

    /** Уникальный идентификатор жанра. */
    private Integer id;

    /** Название жанра (например, Комедия, Драма). */
    private String name;
}
