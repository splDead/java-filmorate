package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import java.util.stream.Collectors;

@Slf4j
@Component("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    /** Шаблон JDBC для выполнения SQL-запросов к базе данных H2. */
    private final JdbcTemplate jdbcTemplate;

    /** Маппер для преобразования строк БД в доменную модель фильма. */
    private final FilmMapper filmMapper;

    /** Маппер для сборки и обогащения возрастных рейтингов MPA. */
    private final MpaMapper mpaMapper;

    /** Маппер для сборки и обогащения жанров кино. */
    private final GenreMapper genreMapper;


    /**
     * Добавляет новый фильм в базу данных и сохраняет его жанры.
     *
     * @param film объект фильма для добавления
     * @return сохраненный фильм с присвоенным идентификатором
     */
    @Override
    public Film addFilm(final Film film) {
        String sqlQuery = "INSERT INTO films (name, description, release_date, "
                + "duration, mpa_rating_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(
                    sqlQuery, new String[]{"id"});

            // Выносим индексы в локальные константы для Checkstyle
            final int releaseDateIdx = 3;
            final int durationIdx = 4;
            final int mpaRatingIdx = 5;

            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(releaseDateIdx, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(durationIdx, film.getDuration());

            if (film.getMpa() != null) {
                stmt.setInt(mpaRatingIdx, film.getMpa().getId());
            } else {
                stmt.setNull(mpaRatingIdx, java.sql.Types.INTEGER);
            }

            return stmt;
        }, keyHolder);

        long generatedId = Objects.requireNonNull(keyHolder.getKey())
                .longValue();
        film.setId(generatedId);

        saveGenres(film);

        log.info("Фильм успешно сохранен в БД с ID: {}", generatedId);
        return film;
    }


    /**
     * Обновляет данные существующего фильма,
     * включая перезапись его жанров и MPA.
     *
     * @param film объект фильма с обновленными данными
     * @return обновленный фильм с полной информацией
     */
    @Override
    public Film update(final Film film) {
        String sqlUpdateFilm = "UPDATE films SET name = ?, description = ?, "
                + "release_date = ?, duration = ?, mpa_rating_id = ?"
                + " WHERE id = ?";

        Integer mpaId = (film.getMpa() != null) ? film.getMpa().getId() : null;

        int rowsUpdated = jdbcTemplate.update(sqlUpdateFilm,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                mpaId,
                film.getId());

        if (rowsUpdated == 0) {
            log.error("Фильм с ID {} не найден для обновления", film.getId());
            throw new NotFoundException("Фильм с указанным ID не найден");
        }

        String sqlDeleteGenres = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlDeleteGenres, film.getId());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            saveGenres(film);

            String sqlSelectGenres =
                    "SELECT g.id, g.name AS name FROM genres g "
                    + "JOIN film_genres fg ON g.id = fg.genre_id "
                    + "WHERE fg.film_id = ? ORDER BY g.id";

            List<Genre> fullGenres = jdbcTemplate.query(
                    sqlSelectGenres, genreMapper::mapRowToGenre, film.getId());

            film.setGenres(new LinkedHashSet<>(fullGenres));
        } else {
            film.setGenres(new LinkedHashSet<>());
        }

        if (mpaId != null) {
            Mpa fullMpa = jdbcTemplate.queryForObject(
                    "SELECT id, name, description"
                            + " FROM mpa_ratings WHERE id = ?",
                    mpaMapper::mapRowToMpa,
                    mpaId
            );
            film.setMpa(fullMpa);
        }

        log.info("Данные фильма с ID {} успешно обновлены", film.getId());
        return film;
    }

    /**
     * Возвращает коллекцию всех фильмов из базы данных
     * с пакетной загрузкой их жанров.
     *
     * @return коллекция всех фильмов с заполненными жанрами и MPA
     */
    @Override
    public Collection<Film> getAllFilms() {
        String sqlQuery = "SELECT f.id, f.name, f.description, f.release_date, "
                + "f.duration, f.mpa_rating_id, m.name AS mpa_name "
                + "FROM films f LEFT JOIN mpa_ratings m "
                + "ON f.mpa_rating_id = m.id";

        List<Film> films = jdbcTemplate.query(
                sqlQuery,
                filmMapper::mapRowToFilm
        );

        if (films.isEmpty()) {
            return films;
        }

        Map<Long, Film> filmMap = films.stream()
                .collect(Collectors.toMap(Film::getId, film -> film));

        String inIds = films.stream()
                .map(f -> String.valueOf(f.getId()))
                .collect(Collectors.joining(","));

        String genreSqlQuery = "SELECT fg.film_id, g.id AS genre_id, "
                + "g.name AS genre_name FROM genres g "
                + "JOIN film_genres fg ON g.id = fg.genre_id "
                + "WHERE fg.film_id IN (" + inIds + ") ORDER BY g.id";

        jdbcTemplate.query(genreSqlQuery, (final ResultSet rs) -> {
            long filmId = rs.getLong("film_id");
            Genre genre = new Genre(
                    rs.getInt("genre_id"), rs.getString("genre_name"));

            Film film = filmMap.get(filmId);
            if (film != null) {
                film.getGenres().add(genre);
            }
        });

        return films;
    }

    /**
     * Сохраняет связи между фильмом и его жанрами в промежуточную таблицу.
     *
     * @param film объект фильма, жанры которого необходимо сохранить
     */
    private void saveGenres(final Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }
        String sqlQuery = "INSERT INTO film_genres (film_id, genre_id) "
                + "VALUES (?, ?)";

        List<Object[]> batchArgs = film.getGenres().stream()
                .map(genre -> new Object[]{film.getId(), genre.getId()})
                .collect(Collectors.toList());

        jdbcTemplate.batchUpdate(sqlQuery, batchArgs);
    }

    /**
     * Возвращает список жанров для конкретного фильма по его идентификатору.
     *
     * @param filmId уникальный идентификатор фильма
     * @return список жанров, привязанных к фильму
     */
    private List<Genre> getGenresByFilmId(final Long filmId) {
        String sqlQuery = "SELECT g.id, g.name FROM genres g "
                + "JOIN film_genres fg ON g.id = fg.genre_id "
                + "WHERE fg.film_id = ? ORDER BY g.id";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> new Genre(
                rs.getInt("id"),
                rs.getString("name")
        ), filmId);
    }

    /**
     * Находит фильм по его уникальному идентификатору и подгружает его жанры.
     *
     * @param id уникальный идентификатор фильма
     * @return Optional, содержащий найденный фильм,
     * или empty, если фильм не найден
     */
    @Override
    public Optional<Film> findFilmById(final Long id) {
        String sqlQuery = "SELECT f.id, f.name, f.description, f.release_date, "
                + "f.duration, f.mpa_rating_id, m.name AS mpa_name "
                + "FROM films f LEFT JOIN mpa_ratings m "
                + "ON f.mpa_rating_id = m.id "
                + "WHERE f.id = ?";
        try {
            Film film = jdbcTemplate.queryForObject(
                    sqlQuery,
                    filmMapper::mapRowToFilm,
                    id
            );
            if (film != null) {
                String genreSql = "SELECT g.id, g.name AS name FROM genres g "
                        + "JOIN film_genres fg ON g.id = fg.genre_id "
                        + "WHERE fg.film_id = ? ORDER BY g.id";
                List<Genre> genres = jdbcTemplate.query(
                        genreSql,
                        genreMapper::mapRowToGenre,
                        id
                );
                film.setGenres(new LinkedHashSet<>(genres));
            }
            return Optional.ofNullable(film);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Добавляет лайк фильму от конкретного пользователя.
     *
     * @param filmId уникальный идентификатор фильма
     * @param userId уникальный идентификатор пользователя
     */
    @Override
    public void addLike(final Long filmId, final Long userId) {
        String checkSql = "SELECT COUNT(*) FROM film_likes "
                + "WHERE film_id = ? AND user_id = ?";
        Integer count = jdbcTemplate.queryForObject(
                checkSql,
                Integer.class,
                filmId,
                userId
        );

        if (count == null || count == 0) {
            String sql = "INSERT INTO film_likes (film_id, user_id) "
                    + "VALUES (?, ?)";
            jdbcTemplate.update(sql, filmId, userId);
            log.info(
                    "Пользователь с ID {} поставил лайк фильму с ID {}",
                    userId,
                    filmId
            );
        }
    }

    /**
     * Удаляет лайк пользователя у фильма.
     *
     * @param filmId уникальный идентификатор фильма
     * @param userId уникальный идентификатор пользователя
     */
    @Override
    public void removeLike(final Long filmId, final Long userId) {
        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, filmId, userId);

        if (rowsAffected == 0) {
            log.warn("Лайк от пользователя {} у фильма {} "
                    + "не найден для удаления",
                    userId,
                    filmId
            );
            throw new NotFoundException(
                    "Лайк от данного пользователя не найден"
            );
        }
        log.info(
                "Пользователь с ID {} удалил лайк у фильма с ID {}",
                userId,
                filmId
        );
    }

    /**
     * Возвращает список наиболее популярных фильмов по количеству лайков.
     *
     * @param count максимальное количество возвращаемых фильмов
     * @return коллекция популярных фильмов с их жанрами
     */
    @Override
    public Collection<Film> getPopularFilms(final Integer count) {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, "
                + "f.duration, f.mpa_rating_id, m.name AS mpa_name, "
                + "COUNT(fl.user_id) AS likes_count "
                + "FROM films f "
                + "LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.id "
                + "LEFT JOIN film_likes fl ON f.id = fl.film_id "
                + "GROUP BY f.id, m.name "
                + "ORDER BY likes_count DESC LIMIT ?";

        List<Film> films = jdbcTemplate.query(
                sql, filmMapper::mapRowToFilm, count);

        if (!films.isEmpty()) {
            String inIds = films.stream()
                    .map(f -> String.valueOf(f.getId()))
                    .collect(Collectors.joining(","));

            String genreSql = "SELECT fg.film_id, g.id AS genre_id, "
                    + "g.name AS genre_name FROM genres g "
                    + "JOIN film_genres fg ON g.id = fg.genre_id "
                    + "WHERE fg.film_id IN (" + inIds + ") ORDER BY g.id";

            Map<Long, Film> filmMap = films.stream()
                    .collect(Collectors.toMap(Film::getId, f -> f));

            jdbcTemplate.query(genreSql, (final ResultSet rs) -> {
                long filmId = rs.getLong("film_id");
                Film film = filmMap.get(filmId);
                if (film != null) {
                    film.getGenres().add(new Genre(
                            rs.getInt("genre_id"),
                            rs.getString("genre_name")));
                }
            });
        }
        return films;
    }
}
