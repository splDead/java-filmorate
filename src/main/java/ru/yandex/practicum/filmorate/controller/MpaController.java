package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

/**
 * REST-контроллер для работы со справочником рейтингов MPA.
 */
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public final class MpaController {

    /** Сервис для обработки бизнес-логики, связанной с рейтингами MPA. */
    private final MpaService mpaService;

    /**
     * Возвращает список всех существующих рейтингов MPA.
     *
     * @return коллекция DTO объектов рейтингов
     */
    @GetMapping
    public Collection<MpaDto> getAllMpa() {
        return mpaService.getAllMpa();
    }

    /**
     * Возвращает данные конкретного рейтинга MPA по его идентификатору.
     *
     * @param id уникальный идентификатор рейтинга
     * @return DTO объект найденного рейтинга
     */
    @GetMapping("/{id}")
    public MpaDto getMpaById(@PathVariable final Integer id) {
        return mpaService.getMpaById(id);
    }
}
