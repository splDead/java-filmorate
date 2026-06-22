package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
    }

    private Film createValidFilm() {
        Film film = new Film();
        film.setName("Интерстеллар");
        film.setDescription("Прекрасный фильм о космосе и времени.");
        film.setReleaseDate(LocalDate.of(2014, 11, 6));
        film.setDuration(169);
        return film;
    }

    @Test
    void shouldAddFilmWhenFilmIsValid() {
        Film film = createValidFilm();

        Film savedFilm = filmController.addFilm(film);

        assertNotNull(savedFilm);
        assertEquals(1, savedFilm.getId(), "Первому фильму должен присвоиться ID = 1");
        assertEquals(1, filmController.getAllFilms().size());
    }

    @Test
    void shouldThrowExceptionWhenReleaseDateIsBeforeCinemaBirth() {
        Film film = createValidFilm();
        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        ValidationException ex = assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        });
        assertEquals("Дата выхода фильма не может быть раньше 28 декабря 1895 года", ex.getMessage());
    }

    @Test
    void shouldUpdateFilmWhenFilmExistsAndValid() {
        Film film = createValidFilm();
        Film savedFilm = filmController.addFilm(film);

        Film updatedData = createValidFilm();
        updatedData.setId(savedFilm.getId());
        updatedData.setName("Новое название");

        Film result = filmController.update(updatedData);

        assertEquals("Новое название", result.getName());
        assertEquals(savedFilm.getId(), result.getId());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentFilm() {
        Film film = createValidFilm();
        film.setId(999L);

        NotFoundException ex = assertThrows(NotFoundException.class, () -> {
            filmController.update(film);
        });
        assertEquals("Фильм с id = 999 не найден", ex.getMessage());
    }

    @Test
    void shouldReturnAllFilms() {
        assertTrue(filmController.getAllFilms().isEmpty());

        filmController.addFilm(createValidFilm());
        Film secondFilm = createValidFilm();
        secondFilm.setName("Начало");
        filmController.addFilm(secondFilm);

        Collection<Film> allFilms = filmController.getAllFilms();
        assertEquals(2, allFilms.size());
    }
}
