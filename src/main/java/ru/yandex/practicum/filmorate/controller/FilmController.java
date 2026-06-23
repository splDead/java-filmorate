package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.exeption.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.util.IdGenerator;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Контроллер для обработки запросов, связанных с фильмами.
 */
@Slf4j
@RestController
@RequestMapping("/films")
public final class FilmController {

    /** Хранилище фильмов. */
    private final Map<Long, Film> films = new ConcurrentHashMap<>();

    /**
     * Добавляет новый фильм в систему.
     *
     * @param film объект фильма для добавления
     * @return сохраненный фильм с присвоенным ID
     */
    @PostMapping
    public Film addFilm(@Valid @RequestBody final Film film) {
        log.info("Получен запрос на добавление фильма: {}",
                film.getName());

        checkDuplicateFilm(film);

        long newId = IdGenerator.getNextId(films);
        film.setId(newId);
        films.put(film.getId(), film);

        log.info("Фильм успешно добавлен с id = {}", film.getId());

        return film;
    }

    /**
     * Обновляет данные существующего фильма.
     *
     * @param newFilm объект фильма с обновленными данными
     * @return обновленный фильм
     */
    @PutMapping
    public Film update(@Valid @RequestBody final Film newFilm) {
        log.info("Получен запрос на обновление фильма с id = {}",
                newFilm.getId());

        if (newFilm.getId() == null) {
            log.warn("Попытка обновить фильм без id");
            throw new ValidationException("Id должен быть указан");
        }

        if (!films.containsKey(newFilm.getId())) {
            log.warn("Попытка обновить несуществующий фильм с id = {}",
                    newFilm.getId());
            throw new NotFoundException("Фильм с id = "
                    + newFilm.getId() + " не найден");
        }

        checkDuplicateFilmForUpdate(newFilm);

        films.put(newFilm.getId(), newFilm);
        log.info("Информация о фильме с id = {} обновлена",
                newFilm.getId());

        return newFilm;
    }

    /**
     * Возвращает список всех сохраненных фильмов.
     *
     * @return коллекция всех фильмов
     */
    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("Получен запрос на список всех фильмов. Всего: {}",
                films.size());
        return films.values();
    }

    /**
     * Проверяет фильм на уникальность перед сохранением.
     *
     * @param film объект фильма для проверки
     */
    private void checkDuplicateFilm(final Film film) {
        if (films.values().stream().anyMatch(film::equals)) {
            log.warn("Попытка добавить дубликат фильма: {}",
                    film.getName());
            throw new DuplicatedDataException("Этот фильм уже добавлен");
        }
    }

    /**
     * Проверяет уникальность данных фильма при его обновлении.
     *
     * @param newFilm обновленный объект фильма
     */
    private void checkDuplicateFilmForUpdate(final Film newFilm) {
        boolean isDuplicate = films.values().stream()
                .filter(f -> !f.getId().equals(newFilm.getId()))
                .anyMatch(newFilm::equals);

        if (isDuplicate) {
            log.warn("Попытка обновить фильм чужими данными");
            throw new DuplicatedDataException("Фильм с такими "
                    + "данными уже существует");
        }
    }
}
