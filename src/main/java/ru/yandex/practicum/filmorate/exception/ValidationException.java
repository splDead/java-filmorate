package ru.yandex.practicum.filmorate.exception;

/**
 * Исключение, выбрасываемое при нарушении условий валидации данных.
 */
public final class ValidationException extends RuntimeException {

    /**
     * Конструирует новое исключение с заданным сообщением об ошибке.
     *
     * @param message сообщение с подробным описанием причины ошибки
     */
    public ValidationException(final String message) {
        super(message);
    }
}

