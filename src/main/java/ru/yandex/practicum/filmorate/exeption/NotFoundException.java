package ru.yandex.practicum.filmorate.exeption;

/**
 * Исключение, выбрасываемое когда запрашиваемый ресурс не найден.
 */
public final class NotFoundException extends RuntimeException {

    /**
     * Конструирует новое исключение с заданным сообщением об ошибке.
     *
     * @param message сообщение с подробным описанием причины ошибки
     */
    public NotFoundException(final String message) {
        super(message);
    }
}
