package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Доменная модель, описывающая жанр фильма.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Genre {

    /** Уникальный идентификатор жанра. */
    private Integer id;

    /** Название жанра. */
    private String name;
}
