package ru.yandex.practicum.filmorate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс для запуска приложения Filmorate.
 */
@SpringBootApplication
public final class FilmorateApplication {

    /**
     * Приватный конструктор для предотвращения создания экземпляров класса.
     */
    private FilmorateApplication() {
        // Пустой конструктор для утилитарного класса
    }

    /**
     * Точка входа в приложение.
     *
     * @param args аргументы командной строки
     */
    public static void main(final String[] args) {
        SpringApplication.run(FilmorateApplication.class, args);
    }
}
