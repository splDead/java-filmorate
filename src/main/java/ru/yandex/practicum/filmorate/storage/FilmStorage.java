package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

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
     * @return коллекция всех фильмов в хранилище
     */
    Collection<Film> getAllFilms();
}
