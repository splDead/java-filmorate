package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Маппер для преобразования рейтингов MPA между слоями DTO, моделей и БД.
 */
@Component
public final class MpaMapper {

    /**
     * Преобразует объект MpaDto в доменную модель Mpa.
     *
     * @param dto входящий объект переноса данных
     * @return доменная модель рейтинга MPA
     */
    public Mpa toModel(final MpaDto dto) {
        if (dto == null) {
            return null;
        }
        return new Mpa(dto.getId(), dto.getName(), dto.getDescription());
    }

    /**
     * Преобразует доменную модель Mpa в объект MpaDto.
     *
     * @param model доменная модель рейтинга MPA
     * @return объект переноса данных рейтинга MPA
     */
    public MpaDto toDto(final Mpa model) {
        if (model == null) {
            return null;
        }
        return MpaDto.builder()
                .id(model.getId())
                .name(model.getName())
                .description(model.getDescription())
                .build();
    }

    /**
     * Маппит строку результата SQL-запроса в доменную модель рейтинга Mpa.
     *
     * @param rs результат выборки из базы данных
     * @param rowNum номер текущей строки
     * @return собранный объект модели рейтинга MPA
     * @throws SQLException при ошибках чтения данных из ResultSet
     */
    public Mpa mapRowToMpa(final ResultSet rs, final int rowNum)
            throws SQLException {
        return new Mpa(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description")
        );
    }
}
