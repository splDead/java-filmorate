package ru.yandex.practicum.filmorate.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.annotation.ValidReleaseDate;

import java.time.LocalDate;

/**
 * Валидатор для проверки корректности даты релиза фильма.
 * Обеспечивает соблюдение правил бизнес-логики относительно даты выхода.
 */
@Slf4j
public final class ReleaseDateValidator
        implements ConstraintValidator<ValidReleaseDate, LocalDate> {

    /**
     * Официальная дата рождения кинематографа (28 декабря 1895 года).
     */
    private static final LocalDate CINEMA_BIRTH_DAY =
            LocalDate.of(1895, 12, 28);

    /**
     * Проверяет, является ли дата релиза фильма корректной.
     *
     * @param releaseDate дата релиза фильма для проверки
     * @param context контекст выполнения валидатора
     * @return true, если дата корректна, иначе false
     */
    @Override
    public boolean isValid(
            final LocalDate releaseDate,
            final ConstraintValidatorContext context) {
        if (releaseDate == null) {
            log.warn("Отсутствует дата выхода фильма");
            return false;
        }

        if (releaseDate.isBefore(CINEMA_BIRTH_DAY)) {
            log.warn("Дата выхода фильма раньше 1895 года: {}",
                    releaseDate);
            return false;
        }

        return true;
    }
}
