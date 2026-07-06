package ru.yandex.practicum.filmorate.exception;

/**
 * Исключение, выбрасываемое при попытке добавить дублирующиеся данные.
 */
public final class DuplicatedDataException extends RuntimeException {

    /**
     * Конструирует новое исключение с заданным сообщением об ошибке.
     *
     * @param message сообщение с подробным описанием причины ошибки
     */
    public DuplicatedDataException(final String message) {
        super(message);
    }
}
