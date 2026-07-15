package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

/**
 * Контроллер для обработки запросов, связанных с фильмами.
 */
@RestController
@RequestMapping("/films")
public final class FilmController {

    /** Сервис для работы с фильмами. */
    private final FilmService service;

    /**
     * Конструктор контроллера.
     *
     * @param filmService сервис фильмов
     */
    public FilmController(final FilmService filmService) {
        this.service = filmService;
    }

    /**
     * Добавляет новый фильм в систему.
     *
     * @param film объект фильма для добавления
     * @return сохраненный фильм с присвоенным ID
     */
    @PostMapping
    public Film addFilm(@Valid @RequestBody final Film film) {
        return service.addFilm(film);
    }

    /**
     * Обновляет данные существующего фильма.
     *
     * @param newFilm объект фильма с обновленными данными
     * @return обновленный фильм
     */
    @PutMapping
    public Film update(@Valid @RequestBody final Film newFilm) {
        return service.update(newFilm);
    }

    /**
     * Возвращает список всех сохраненных фильмов.
     *
     * @return коллекция всех фильмов
     */
    @GetMapping
    public Collection<Film> getAllFilms() {
        return service.getAllFilms();
    }

    /**
     * Добавляет лайк фильму от пользователя.
     *
     * @param id     идентификатор фильма
     * @param userId идентификатор пользователя
     */
    @PutMapping("/{id}/like/{userId}")
    public void addLike(
            @PathVariable final Long id,
            @PathVariable final Long userId
    ) {
        service.addLike(id, userId);
    }

    /**
     * Удаляет лайк пользователя у фильма.
     *
     * @param id     идентификатор фильма
     * @param userId идентификатор пользователя
     */
    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(
            @PathVariable final Long id,
            @PathVariable final Long userId
    ) {
        service.removeLike(id, userId);
    }

    /**
     * Возвращает список популярных фильмов.
     *
     * @param count количество фильмов для вывода
     * @return список популярных фильмов
     */
    @GetMapping("/popular")
    public List<Film> getPopularFilms(
            @RequestParam(defaultValue = "10") final int count
    ) {
        return service.getPopularFilms(count);
    }
}
