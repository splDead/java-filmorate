package ru.yandex.practicum.filmorate.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

/**
 * Маппер для преобразования объектов фильма между слоями DTO, моделей и БД.
 */
@Component
@RequiredArgsConstructor
public final class FilmMapper {

    /** Маппер для работы с объектами рейтингов MPA. */
    private final MpaMapper mpaMapper;

    /** Маппер для работы с объектами жанров. */
    private final GenreMapper genreMapper;

    /**
     * Преобразует объект FilmDto в доменную модель Film.
     *
     * @param dto входящий объект переноса данных
     * @return доменная модель фильма
     */
    public Film toModel(final FilmDto dto) {
        if (dto == null) {
            return null;
        }

        LinkedHashSet<Genre> modelGenres = new LinkedHashSet<>();
        if (dto.getGenres() != null) {
            modelGenres = dto.getGenres().stream()
                    .map(genreMapper::toModel)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }

        Mpa modelMpa = null;
        if (dto.getMpa() != null) {
            modelMpa = mpaMapper.toModel(dto.getMpa());
        }

        return Film.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .releaseDate(dto.getReleaseDate())
                .duration(dto.getDuration())
                .mpa(modelMpa)
                .genres(modelGenres)
                .build();
    }

    /**
     * Преобразует доменную модель Film в объект FilmDto.
     *
     * @param film доменная модель фильма
     * @return объект переноса данных фильма
     */
    public FilmDto toDto(final Film film) {
        if (film == null) {
            return null;
        }

        LinkedHashSet<GenreDto> dtoGenres = new LinkedHashSet<>();
        if (film.getGenres() != null) {
            dtoGenres = film.getGenres().stream()
                    .map(genreMapper::toDto)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }

        return FilmDto.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .mpa(mpaMapper.toDto(film.getMpa()))
                .genres(dtoGenres)
                .build();
    }

    /**
     * Маппит строку результата SQL-запроса в доменную модель фильма Film.
     *
     * @param rs результат выборки из базы данных
     * @param rowNum номер текущей строки
     * @return собранный объект модели фильма
     * @throws SQLException при ошибках чтения данных из ResultSet
     */
    public Film mapRowToFilm(final ResultSet rs, final int rowNum)
            throws SQLException {
        Mpa mpa = null;
        int mpaId = rs.getInt("mpa_rating_id");

        if (!rs.wasNull() && mpaId > 0) {
            mpa = new Mpa(
                    mpaId,
                    rs.getString("mpa_name"),
                    null
            );
        }

        return Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(mpa)
                .genres(new LinkedHashSet<>())
                .build();
    }
}
