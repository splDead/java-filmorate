package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Mpa;
import java.util.Collection;
import java.util.Optional;

/**
 * Интерфейс для управления хранилищем возрастных рейтингов MPA.
 */
public interface MpaStorage {

    /**
     * Возвращает коллекцию всех возрастных рейтингов MPA.
     *
     * @return коллекция всех доступных рейтингов
     */
    Collection<Mpa> getAllMpa();

    /**
     * Находит возрастной рейтинг MPA по его уникальному идентификатору.
     *
     * @param id уникальный идентификатор рейтинга MPA
     * @return Optional, содержащий найденный рейтинг, или empty
     */
    Optional<Mpa> getMpaById(Integer id);
}
