package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Сервис для обработки бизнес-логики, связанной с жанрами фильмов.
 */
@Service
@RequiredArgsConstructor
public class GenreService {

    /** Хранилище объектов жанров. */
    private final GenreStorage genreStorage;

    /** Маппер для преобразования объектов жанров. */
    private final GenreMapper genreMapper;

    /**
     * Возвращает коллекцию DTO всех существующих жанров.
     *
     * @return коллекция DTO всех жанров
     */
    public Collection<GenreDto> getAllGenres() {
        return genreStorage.getAllGenres().stream()
                .map(genreMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Находит жанр по его уникальному идентификатору.
     *
     * @param id уникальный идентификатор жанра
     * @return DTO объект найденного жанра
     */
    public GenreDto getGenreById(final Integer id) {
        return genreStorage.getGenreById(id)
                .map(genreMapper::toDto)
                .orElseThrow(() -> new NotFoundException(
                        "Жанр с ID " + id + " не найден"));
    }
}
