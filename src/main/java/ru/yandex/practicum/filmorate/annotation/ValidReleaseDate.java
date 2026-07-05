package ru.yandex.practicum.filmorate.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.yandex.practicum.filmorate.util.ReleaseDateValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для валидации даты релиза фильма.
 * Проверяет, что дата не пустая и не раньше появления кинематографа.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ReleaseDateValidator.class)
@Documented
public @interface ValidReleaseDate {

    /**
     * Возвращает дефолтное сообщение об ошибке валидации.
     *
     * @return сообщение об ошибке
     */
    String message() default "Дата выхода фильма не может "
            + "быть пустой или раньше 28 декабря 1895 года";

    /**
     * Позволяет разделять группы валидации.
     *
     * @return группы валидации
     */
    Class<?>[] groups() default {};

    /**
     * Используется для передачи метаданных клиенту.
     *
     * @return полезная нагрузка
     */
    Class<? extends Payload>[] payload() default {};
}
