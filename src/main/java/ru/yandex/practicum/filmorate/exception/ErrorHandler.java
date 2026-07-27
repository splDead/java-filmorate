package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Глобальный обработчик исключений приложения.
 */
@Slf4j
@RestControllerAdvice
public final class ErrorHandler {

    /**
     * Обрабатывает ошибки валидации ValidationException.
     *
     * @param e исключение валидации
     * @return карта с описанием ошибки
     */
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(
            final ValidationException e
    ) {
        log.warn("Ошибка валидации: {}", e.getMessage());
        return Map.of("error", "Ошибка валидации",
                "message", e.getMessage());
    }

    /**
     * Обрабатывает исключения отсутствия объектов NotFoundException.
     *
     * @param e исключение отсутствия объекта
     * @return карта с описанием ошибки
     */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(
            final NotFoundException e
    ) {
        log.warn("Объект не найден: {}", e.getMessage());
        return Map.of("error", "Искомый объект не найден",
                "message", e.getMessage());
    }

    /**
     * Обрабатывает все непредвиденные системные исключения.
     *
     * @param e непредвиденное исключение
     * @return карта с описанием ошибки
     */
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleThrowable(final Throwable e) {
        log.error("Непредвиденная ошибка сервера", e);
        return Map.of("error", "Внутреннее исключение сервера",
                "message", e.getMessage());
    }

    /**
     * Обрабатывает ошибки некорректного или отсутствующего JSON.
     *
     * @param e исключение нечитаемого HTTP-сообщения
     * @return карта с описанием ошибки
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleHttpMessageNotReadableException(
            final HttpMessageNotReadableException e
    ) {
        log.warn("Ошибка 400 Bad Request: Некорректный JSON: {}",
                e.getMessage());
        return Map.of(
                "error", "Тело запроса отсутствует или не читается",
                "message", e.getMessage()
        );
    }

    /**
     * Обрабатывает ошибки стандартной валидации полей аннотаций.
     *
     * @param e исключение невалидных аргументов метода
     * @return карта с описанием ошибки
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException e
    ) {
        StringBuilder message = new StringBuilder();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            message.append(fieldError.getField())
                    .append(": ")
                    .append(fieldError.getDefaultMessage())
                    .append("; ");
        }

        log.warn("Ошибка валидации: {}", message);
        return Map.of(
                "error", "Ошибка валидации параметров объекта",
                "message", message.toString()
        );
    }

    /**
     * Обрабатывает исключения некорректных аргументов.
     *
     * @param e исключение некорректного аргумента
     * @return карта с описанием ошибки
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleIllegalArgumentException(
            final IllegalArgumentException e
    ) {
        log.warn("Некорректный аргумент: {}", e.getMessage());
        return Map.of("error", "Некорректный параметр запроса",
                "message", e.getMessage());
    }

    /**
     * Обрабатывает ошибки нарушения целостности данных.
     *
     * @param e исключение нарушения целостности данных
     * @return карта с описанием ошибки
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleDataIntegrityViolationException(
            final DataIntegrityViolationException e
    ) {
        log.warn("Нарушение целостности данных в БД: {}", e.getMessage());
        return Map.of("error", "Связанный объект не найден в справочниках",
                "message", "Несуществующий ID связанной сущности");
    }
}
