package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

/**
 * REST-контроллер для работы со справочником жанров.
 */
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public final class GenreController {

    /** Сервис для обработки бизнес-логики, связанной с жанрами. */
    private final GenreService genreService;

    /**
     * Возвращает список всех существующих жанров кино.
     *
     * @return коллекция DTO объектов жанров
     */
    @GetMapping
    public Collection<GenreDto> getAllGenres() {
        return genreService.getAllGenres();
    }

    /**
     * Возвращает данные конкретного жанра по его идентификатору.
     *
     * @param id уникальный идентификатор жанра
     * @return DTO объект найденного жанра
     */
    @GetMapping("/{id}")
    public GenreDto getGenreById(@PathVariable final Integer id) {
        return genreService.getGenreById(id);
    }
}
