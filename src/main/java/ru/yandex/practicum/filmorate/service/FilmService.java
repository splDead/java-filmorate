package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Сервис для обработки бизнес-логики, связанной с фильмами.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    /** Хранилище фильмов. */
    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;

    /** Хранилище пользователей. */
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    /** Маппер для преобразования объектов фильмов. */
    private final FilmMapper filmMapper;

    /**
     * Добавляет новый фильм в систему.
     *
     * @param filmDto DTO объект добавляемого фильма
     * @return DTO объект сохраненного фильма
     */
    public FilmDto addFilm(final FilmDto filmDto) {
        log.info("Получен запрос на добавление фильма: {}", filmDto.getName());

        Film film = filmMapper.toModel(filmDto);
        Film savedFilm = filmStorage.addFilm(film);

        return filmMapper.toDto(savedFilm);
    }

    /**
     * Обновляет данные существующего фильма.
     *
     * @param filmDto DTO объект с обновленными данными
     * @return DTO объект обновленного фильма
     */
    public FilmDto updateFilm(final FilmDto filmDto) {
        log.info("Получен запрос на обновление фильма с ID: {}",
                filmDto.getId());
        if (filmDto.getId() == null) {
            throw new IllegalArgumentException(
                    "ID фильма не может быть пустым при обновлении");
        }

        Film film = filmMapper.toModel(filmDto);
        Film updatedFilm = filmStorage.update(film);

        return filmMapper.toDto(updatedFilm);
    }

    /**
     * Возвращает коллекцию всех доступных фильмов.
     *
     * @return коллекция DTO всех фильмов
     */
    public Collection<FilmDto> getAllFilms() {
        log.info("Получен запрос на получение всех фильмов");
        return filmStorage.getAllFilms().stream()
                .map(filmMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Находит фильм по его уникальному идентификатору.
     *
     * @param id уникальный идентификатор фильма
     * @return DTO объект найденного фильма
     */
    public FilmDto findFilmById(final Long id) {
        Film film = filmStorage.findFilmById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Фильм с ID " + id + " не найден"));
        return filmMapper.toDto(film);
    }

    /**
     * Добавляет лайк фильму от пользователя.
     *
     * @param filmId уникальный идентификатор фильма
     * @param userId уникальный идентификатор пользователя
     */
    public void addLike(final Long filmId, final Long userId) {
        log.info("Добавление лайка фильму {} от пользователя {}",
                filmId, userId);

        filmStorage.findFilmById(filmId)
                .orElseThrow(() -> new NotFoundException(
                        "Фильм с ID " + filmId + " не найден"));

        userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException(
                        "Пользователь с ID " + userId + " не найден"));

        filmStorage.addLike(filmId, userId);
    }

    /**
     * Удаляет лайк пользователя у фильма.
     *
     * @param filmId уникальный идентификатор фильма
     * @param userId unique идентификатор пользователя
     */
    public void removeLike(final Long filmId, final Long userId) {
        log.info("Удаление лайка у фильма {} от пользователя {}",
                filmId, userId);

        filmStorage.findFilmById(filmId)
                .orElseThrow(() -> new NotFoundException(
                        "Фильм с ID " + filmId + " не найден"));

        userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException(
                        "Пользователь с ID " + userId + " не найден"));

        filmStorage.removeLike(filmId, userId);
    }

    /**
     * Возвращает список наиболее популярных фильмов по количеству лайков.
     *
     * @param count максимальное лимитированное количество фильмов
     * @return коллекция DTO популярных фильмов
     */
    public Collection<FilmDto> getPopularFilms(final Integer count) {
        log.info("Получение списка {} самых популярных фильмов", count);
        return filmStorage.getPopularFilms(count).stream()
                .map(filmMapper::toDto)
                .collect(Collectors.toList());
    }
}
