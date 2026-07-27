package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Модель возрастного рейтинга ассоциации кинокомпаний (MPA).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mpa {

    /** Уникальный идентификатор возрастного рейтинга. */
    private Integer id;

    /** Короткое наименование рейтинга (например: G, PG, R). */
    private String name;

    /** Подробное описание ограничений рейтинга. */
    private String description;
}
