package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class FilmControllerTest {

    private FilmController filmController;
    private FilmService filmService;
    private Validator validator;

    @BeforeEach
    void setUp() {
        filmService = Mockito.mock(FilmService.class);
        filmController = new FilmController(filmService);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    private FilmDto createValidFilmDto() {
        return FilmDto.builder()
                .name("Интерстеллар")
                .description("Прекрасный фильм о космосе и времени.")
                .releaseDate(LocalDate.of(2014, 11, 6))
                .duration(169)
                .mpa(MpaDto.builder().id(3).name("PG-13").build())
                .build();
    }

    @Test
    void shouldAddFilmWhenFilmIsValid() {
        FilmDto inputDto = createValidFilmDto();
        FilmDto savedDto = createValidFilmDto();
        savedDto.setId(1L);

        when(filmService.addFilm(any(FilmDto.class))).thenReturn(savedDto);
        when(filmService.getAllFilms()).thenReturn(List.of(savedDto));

        FilmDto result = filmController.addFilm(inputDto);

        assertNotNull(result);
        assertEquals(1L, result.getId(), "Первому фильму должен присвоиться ID = 1");
        assertEquals(1, filmController.getAllFilms().size());
    }

    @Test
    void shouldThrowExceptionWhenReleaseDateIsBeforeCinemaBirth() {
        FilmDto dto = createValidFilmDto();
        dto.setReleaseDate(LocalDate.of(1895, 12, 27));

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "Ожидалась ошибка валидации для даты до 1895 года");
        assertEquals(1, violations.size());

        String expectedMessage = "Дата выхода фильма не может быть пустой или раньше 28 декабря 1895 года";
        String actualMessage = violations.iterator().next().getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldPassValidationWithCorrectReleaseDate() {
        FilmDto dto = createValidFilmDto();
        dto.setReleaseDate(LocalDate.of(1895, 12, 28));
        Set<ConstraintViolation<FilmDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Валидация должна пройти успешно для даты 28.12.1895");
    }

    @Test
    void shouldUpdateFilmWhenFilmExistsAndValid() {
        FilmDto updatedData = createValidFilmDto();
        updatedData.setId(1L);
        updatedData.setName("Новое название");

        when(filmService.updateFilm(any(FilmDto.class))).thenReturn(updatedData);

        FilmDto result = filmController.updateFilm(updatedData);

        assertEquals("Новое название", result.getName());
        assertEquals(1L, result.getId());
    }

    @Test
    void shouldReturnAllFilms() {
        FilmDto firstFilm = createValidFilmDto();
        firstFilm.setId(1L);
        FilmDto secondFilm = createValidFilmDto();
        secondFilm.setId(2L);
        secondFilm.setName("Начало");

        when(filmService.getAllFilms()).thenReturn(List.of(firstFilm, secondFilm));

        Collection<FilmDto> allFilms = filmController.getAllFilms();
        assertEquals(2, allFilms.size());
    }

    @Test
    void shouldReturnFilmById() {
        FilmDto filmDto = createValidFilmDto();
        filmDto.setId(4L);

        when(filmService.findFilmById(4L)).thenReturn(filmDto);

        FilmDto result = filmController.findFilmById(4L);

        assertNotNull(result);
        assertEquals(4L, result.getId());
        assertEquals("Интерстеллар", result.getName());
    }

    @Test
    void shouldCallServiceToAddLike() {
        doNothing().when(filmService).addLike(anyLong(), anyLong());

        filmController.addLike(4L, 1L);

        verify(filmService, times(1)).addLike(4L, 1L);
    }

    @Test
    void shouldCallServiceToRemoveLike() {
        doNothing().when(filmService).removeLike(anyLong(), anyLong());

        filmController.removeLike(4L, 1L);

        verify(filmService, times(1)).removeLike(4L, 1L);
    }

    @Test
    void shouldReturnPopularFilms() {
        FilmDto popularFilm = createValidFilmDto();
        popularFilm.setId(4L);
        popularFilm.setName("Популярный фильм");

        when(filmService.getPopularFilms(anyInt())).thenReturn(List.of(popularFilm));

        Collection<FilmDto> result = filmController.getPopularFilms(1);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Популярный фильм", result.iterator().next().getName());
        verify(filmService, times(1)).getPopularFilms(1);
    }
}
