package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Объект переноса данных (DTO) для возрастного рейтинга MPA.
 */
@Data
@Builder
public class MpaDto {

    /** Уникальный идентификатор рейтинга. */
    private Integer id;

    /** Краткое наименование рейтинга (например, G, PG, R). */
    private String name;

    /** Подробное описание возрастного ограничения. */
    private String description;
}
