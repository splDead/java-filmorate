package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.util.IdGenerator;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Хранилище фильмов в оперативной памяти.
 */
@Slf4j
@Component
public final class InMemoryFilmStorage implements FilmStorage {

    /** Карта для хранения фильмов, где ключ — идентификатор фильма. */
    private final Map<Long, Film> films = new ConcurrentHashMap<>();

    @Override
    public Film addFilm(final Film film) {
        log.info("Получен запрос на добавление фильма: {}",
                film.getName());

        checkDuplicateFilm(film);

        long newId = IdGenerator.getNextId(films);
        film.setId(newId);
        films.put(film.getId(), film);

        log.info("Фильм успешно добавлен с id = {}", film.getId());

        return film;
    }

    @Override
    public Film update(final Film newFilm) {
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

    @Override
    public Collection<Film> getAllFilms() {
        log.info("Получен запрос на список всех фильмов. Всего: {}",
                films.size());
        return films.values();
    }

    /**
     * Находит фильм по его идентификатору.
     *
     * @param id идентификатор искомого фильма
     * @return найденный фильм или null, если фильм не найден
     */
    public Film findById(final Long id) {
        return films.get(id);
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
     * @param newFilm updated объект фильма
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
