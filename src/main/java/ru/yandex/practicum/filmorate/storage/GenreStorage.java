package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;
import java.util.Collection;
import java.util.Optional;

/**
 * Интерфейс для управления хранилищем жанров фильмов.
 */
public interface GenreStorage {

    /**
     * Возвращает коллекцию всех жанров, зарегистрированных в системе.
     *
     * @return коллекция всех доступных жанров
     */
    Collection<Genre> getAllGenres();

    /**
     * Находит жанр по его уникальному идентификатору.
     *
     * @param id уникальный идентификатор жанра
     * @return Optional, содержащий найденный жанр,
     * или empty, если жанр не найден
     */
    Optional<Genre> getGenreById(Integer id);
}
