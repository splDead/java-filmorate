package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Optional;

/**
 * Имплементация хранилища рейтингов MPA, работающая с базой данных H2.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    /** Шаблон JDBC для выполнения SQL-запросов к базе данных. */
    private final JdbcTemplate jdbcTemplate;

    /** Маппер для сборки доменных моделей рейтингов MPA из ResultSet. */
    private final MpaMapper mpaMapper;

    /**
     * Возвращает коллекцию всех рейтингов MPA, отсортированных по ID.
     *
     * @return коллекция всех доступных возрастных рейтингов
     */
    @Override
    public Collection<Mpa> getAllMpa() {
        String sql = "SELECT id, name, description "
                + "FROM mpa_ratings ORDER BY id";
        return jdbcTemplate.query(sql, mpaMapper::mapRowToMpa);
    }

    /**
     * Находит возрастной рейтинг MPA по его уникальному идентификатору.
     *
     * @param id уникальный идентификатор рейтинга MPA
     * @return Optional, содержащий найденный рейтинг, или empty
     */
    @Override
    public Optional<Mpa> getMpaById(final Integer id) {
        String sql = "SELECT id, name, description "
                + "FROM mpa_ratings WHERE id = ?";
        try {
            Mpa mpa = jdbcTemplate.queryForObject(
                    sql, mpaMapper::mapRowToMpa, id);
            return Optional.ofNullable(mpa);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
