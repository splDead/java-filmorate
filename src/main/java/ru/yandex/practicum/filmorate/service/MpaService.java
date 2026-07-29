package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Сервис для обработки бизнес-логики, связанной с рейтингами MPA.
 */
@Service
@RequiredArgsConstructor
public class MpaService {

    /** Хранилище объектов рейтингов MPA. */
    private final MpaStorage mpaStorage;

    /** Маппер для преобразования объектов рейтингов MPA. */
    private final MpaMapper mpaMapper;

    /**
     * Возвращает коллекцию DTO всех существующих рейтингов MPA.
     *
     * @return коллекция DTO всех рейтингов MPA
     */
    public Collection<MpaDto> getAllMpa() {
        return mpaStorage.getAllMpa().stream()
                .map(mpaMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Находит рейтинг MPA по его уникальному идентификатору.
     *
     * @param id уникальный идентификатор рейтинга MPA
     * @return DTO объект найденного рейтинга MPA
     */
    public MpaDto getMpaById(final Integer id) {
        return mpaStorage.getMpaById(id)
                .map(mpaMapper::toDto)
                .orElseThrow(() -> new NotFoundException(
                        "Рейтинг MPA с ID " + id + " не найден"));
    }
}
