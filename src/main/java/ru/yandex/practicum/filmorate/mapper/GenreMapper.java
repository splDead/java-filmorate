package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Маппер для преобразования объектов жанров между слоями DTO, моделей и БД.
 */
@Component
public final class GenreMapper {

    /**
     * Преобразует объект GenreDto в доменную модель Genre.
     *
     * @param dto входящий объект переноса данных
     * @return доменная модель жанра
     */
    public Genre toModel(final GenreDto dto) {
        if (dto == null) {
            return null;
        }
        return new Genre(dto.getId(), dto.getName());
    }

    /**
     * Преобразует доменную модель Genre в объект GenreDto.
     *
     * @param model доменная модель жанра
     * @return объект переноса данных жанра
     */
    public GenreDto toDto(final Genre model) {
        if (model == null) {
            return null;
        }
        return GenreDto.builder()
                .id(model.getId())
                .name(model.getName())
                .build();
    }

    /**
     * Маппит строку результата SQL-запроса в доменную модель жанра Genre.
     *
     * @param rs результат выборки из базы данных
     * @param rowNum номер текущей строки
     * @return собранный объект модели жанра
     * @throws SQLException при ошибках чтения данных из ResultSet
     */
    public Genre mapRowToGenre(final ResultSet rs, final int rowNum)
            throws SQLException {
        return new Genre(
                rs.getInt("id"),
                rs.getString("name")
        );
    }
}
