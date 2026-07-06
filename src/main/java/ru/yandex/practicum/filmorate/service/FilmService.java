package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервисный класс для управления операциями с фильмами.
 */
@Service
public final class FilmService {

    /** Хранилище фильмов. */
    private final InMemoryFilmStorage filmStorage;

    /** Хранилище пользователей. */
    private final InMemoryUserStorage userStorage;

    /**
     * Конструктор сервиса.
     *
     * @param initialFilmStorage Хранилище фильмов
     * @param initialUserStorage Хранилище пользователей
     */
    public FilmService(
            final InMemoryFilmStorage initialFilmStorage,
            final InMemoryUserStorage initialUserStorage
    ) {
        this.filmStorage = initialFilmStorage;
        this.userStorage = initialUserStorage;
    }

    /**
     * Добавляет новый фильм в систему.
     *
     * @param film объект фильма для добавления
     * @return сохраненный фильм с присвоенным ID
     */
    public Film addFilm(final Film film) {
        return filmStorage.addFilm(film);
    }

    /**
     * Обновляет данные существующего фильма.
     *
     * @param newFilm объект фильма с обновленными данными
     * @return обновленный фильм
     */
    public Film update(final Film newFilm) {
        return filmStorage.update(newFilm);
    }

    /**
     * Возвращает список всех сохраненных фильмов.
     *
     * @return коллекция всех фильмов
     */
    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    /**
     * Добавление лайка фильму от пользователя.
     *
     * @param filmId идентификатор фильма
     * @param userId идентификатор пользователя
     */
    public void addLike(final Long filmId, final Long userId) {
        Film film = filmStorage.findById(filmId);

        if (film == null) {
            throw new NotFoundException("Фильм с id=" + filmId + " не найден");
        }

        if (userStorage.findById(userId) == null) {
            throw new NotFoundException("Пользователь с id=" + userId
                    + " не найден");
        }

        film.getLikes().add(userId);
    }

    /**
     * Удаление лайка у фильма.
     *
     * @param filmId идентификатор фильма
     * @param userId идентификатор пользователя
     */
    public void removeLike(final Long filmId, final Long userId) {
        Film film = filmStorage.findById(filmId);

        if (film == null) {
            throw new NotFoundException("Фильм с id=" + filmId + " не найден");
        }

        if (userStorage.findById(userId) == null) {
            throw new NotFoundException("Пользователь с id=" + userId
                    + " не найден");
        }

        film.getLikes().remove(userId);
    }

    /**
     * Возвращает список самых популярных фильмов.
     *
     * @param count количество фильмов для вывода
     * @return список популярных фильмов
     */
    public List<Film> getPopularFilms(final int count) {
        return filmStorage.getAllFilms().stream()
                .sorted((f1, f2) -> Integer.compare(
                        f2.getLikes().size(),
                        f1.getLikes().size()
                ))
                .limit(count)
                .collect(Collectors.toList());
    }
}
