package ru.yandex.practicum.filmorate.util;

import java.util.Map;

/**
 * Утилитарный класс для генерации уникальных идентификаторов.
 */
public final class IdGenerator {

    /**
     * Приватный конструктор для предотвращения создания экземпляров.
     */
    private IdGenerator() {
        throw new UnsupportedOperationException(
                "Это утилитарный класс, его нельзя создавать через new");
    }

    /**
     * Генерирует следующий уникальный ID на основе существующего хранилища.
     *
     * @param storage карта с текущими сохраненными данными
     * @return следующий свободный идентификатор
     */
    public static long getNextId(final Map<Long, ?> storage) {
        long currentMaxId = storage.keySet()
                .stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
