package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

/**
 * Интерфейс для управления хранилищем фильмов.
 */
public interface FilmStorage {

    /**
     * Добавляет новый фильм в хранилище.
     *
     * @param film объект фильма для добавления
     * @return сохраненный фильм с присвоенным идентификатором
     */
    Film addFilm(Film film);

    /**
     * Обновляет данные существующего фильма в хранилище.
     *
     * @param film объект фильма с обновленными данными
     * @return обновленный фильм
     */
    Film update(Film film);

    /**
     * Возвращает коллекцию всех сохраненных фильмов.
     *
     * @return collection всех фильмов в хранилище
     */
    Collection<Film> getAllFilms();

    /**
     * Находит фильм по его уникальному идентификатору.
     *
     * @param id уникальный идентификатор фильма
     * @return Optional, содержащий найденный фильм,
     * или empty, если фильм не найден
     */
    Optional<Film> findFilmById(Long id);

    /**
     * Добавляет лайк фильму от конкретного пользователя.
     *
     * @param filmId уникальный идентификатор фильма
     * @param userId уникальный идентификатор пользователя
     */
    void addLike(Long filmId, Long userId);

    /**
     * Удаляет лайк пользователя у фильма.
     *
     * @param filmId уникальный идентификатор фильма
     * @param userId уникальный идентификатор пользователя
     */
    void removeLike(Long filmId, Long userId);

    /**
     * Возвращает список наиболее популярных фильмов по количеству лайков.
     *
     * @param count максимальное количество возвращаемых фильмов
     * @return коллекция популярных фильмов
     */
    Collection<Film> getPopularFilms(Integer count);
}
