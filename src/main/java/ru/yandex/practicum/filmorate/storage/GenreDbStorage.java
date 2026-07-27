package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

/**
 * Имплементация хранилища жанров, работающая с реляционной базой данных.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    /** Шаблон JDBC для выполнения запросов к базе данных. */
    private final JdbcTemplate jdbcTemplate;

    /** Маппер для сборки доменных моделей жанров из ResultSet. */
    private final GenreMapper genreMapper;

    /**
     * Возвращает коллекцию всех жанров, отсортированных по идентификатору.
     *
     * @return коллекция всех доступных жанров
     */
    @Override
    public Collection<Genre> getAllGenres() {
        String sql = "SELECT id, name FROM genres ORDER BY id";
        return jdbcTemplate.query(sql, genreMapper::mapRowToGenre);
    }

    /**
     * Находит жанр по его уникальному идентификатору.
     *
     * @param id уникальный идентификатор жанра
     * @return Optional, содержащий найденный жанр,
     * или empty, если жанр не найден
     */
    @Override
    public Optional<Genre> getGenreById(final Integer id) {
        String sql = "SELECT id, name FROM genres WHERE id = ?";
        try {
            Genre genre = jdbcTemplate.queryForObject(
                    sql, genreMapper::mapRowToGenre, id);
            return Optional.ofNullable(genre);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
