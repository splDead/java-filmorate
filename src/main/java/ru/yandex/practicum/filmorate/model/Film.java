package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.yandex.practicum.filmorate.annotation.ValidReleaseDate;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Класс, описывающий модель фильма.
 */
@Data
@Builder
@AllArgsConstructor
public final class Film {

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
    @ValidReleaseDate
    private LocalDate releaseDate;

    /** Продолжительность фильма в минутах. */
    @NotNull
    @Positive
    private Integer duration;

    /** Рейтинг MPA. Соответствует полю mpa_rating_id в таблице films. */
    private Mpa mpa;

    /** Жанры фильма. Связываются через промежуточную таблицу film_genres. */
    @Builder.Default
    private LinkedHashSet<Genre> genres = new LinkedHashSet<>();

    /** Список идентификаторов пользователей, поставивших лайк. */
    @Getter(lombok.AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<Long> likes = ConcurrentHashMap.newKeySet();

    /**
     * Возвращает список лайков фильма.
     * Если список равен null, инициализирует его пустым множеством.
     *
     * @return множество идентификаторов пользователей, лайкнувших фильм
     */
    public Set<Long> getLikes() {
        if (likes == null) {
            likes = ConcurrentHashMap.newKeySet();
        }
        return likes;
    }
}
