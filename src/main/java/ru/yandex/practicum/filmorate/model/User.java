package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Класс, описывающий модель пользователя.
 */
@Data
@Builder
@AllArgsConstructor
public final class User {

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

    /** Список друзей. */
    @Getter(lombok.AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<Long> friends = ConcurrentHashMap.newKeySet();

    /**
     * Возвращает список идентификаторов друзей пользователя.
     * Если список равен null, инициализирует его пустым множеством.
     *
     * @return множество идентификаторов друзей
     */
    public Set<Long> getFriends() {
        if (friends == null) {
            friends = ConcurrentHashMap.newKeySet();
        }
        return friends;
    }
}
