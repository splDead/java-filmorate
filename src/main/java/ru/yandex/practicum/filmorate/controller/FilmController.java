package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

/**
 * REST-контроллер для обработки запросов, связанных с фильмами.
 */
@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public final class FilmController {

    /** Сервис для работы с бизнес-логикой фильмов. */
    private final FilmService service;

    /**
     * Добавляет новый фильм в систему.
     *
     * @param filmDto данные добавляемого фильма
     * @return добавленный фильм с присвоенным идентификатором
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDto addFilm(@Valid @RequestBody final FilmDto filmDto) {
        log.info("REST-запрос на добавление фильма: {}",
                filmDto.getName());
        return service.addFilm(filmDto);
    }

    /**
     * Обновляет данные существующего фильма.
     *
     * @param filmDto updated данные фильма
     * @return обновленный объект фильма
     */
    @PutMapping
    public FilmDto updateFilm(
            @Valid @RequestBody final FilmDto filmDto) {
        log.info("REST-запрос на обновление фильма с ID: {}",
                filmDto.getId());
        return service.updateFilm(filmDto);
    }

    /**
     * Возвращает коллекцию всех доступных фильмов.
     *
     * @return коллекция всех фильмов
     */
    @GetMapping
    public Collection<FilmDto> getAllFilms() {
        log.info("REST-запрос на получение всех фильмов");
        return service.getAllFilms();
    }

    /**
     * Находит фильм по его идентификатору.
     *
     * @param id уникальный идентификатор фильма
     * @return найденный фильм
     */
    @GetMapping("/{id}")
    public FilmDto findFilmById(@PathVariable final Long id) {
        log.info("REST-запрос: получение фильма с ID: {}", id);
        return service.findFilmById(id);
    }

    /**
     * Добавляет лайк фильму от конкретного пользователя.
     *
     * @param id уникальный идентификатор фильма
     * @param userId уникальный идентификатор пользователя
     */
    @PutMapping("/{id}/like/{userId}")
    public void addLike(
            @PathVariable final Long id,
            @PathVariable final Long userId) {
        log.info("REST-запрос: пользователь {} ставит лайк фильму {}",
                userId, id);
        service.addLike(id, userId);
    }

    /**
     * Удаляет лайк пользователя у фильма.
     *
     * @param id уникальный идентификатор фильма
     * @param userId уникальный идентификатор пользователя
     */
    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(
            @PathVariable final Long id,
            @PathVariable final Long userId) {
        log.info("REST-запрос: пользователь {} удаляет лайк у фильма {}",
                userId, id);
        service.removeLike(id, userId);
    }

    /**
     * Возвращает список самых популярных фильмов на основе лайков.
     *
     * @param count максимальное количество фильмов в результате
     * @return коллекция популярных фильмов
     */
    @GetMapping("/popular")
    public Collection<FilmDto> getPopularFilms(
            @RequestParam(defaultValue = "10") final Integer count) {
        log.info("REST-запрос: получение популярных фильмов. Количестко: {}",
                count);
        return service.getPopularFilms(count);
    }
}
