package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 * Имплементация хранилища пользователей, работающая с базой данных H2.
 */
@Slf4j
@Component("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    /** Шаблон JDBC для выполнения SQL-запросов к БД. */
    private final JdbcTemplate jdbcTemplate;

    /** Маппер для сборки сущностей пользователей. */
    private final UserMapper userMapper;

    /**
     * Добавляет нового пользователя в базу данных.
     *
     * @param user доменная модель пользователя для добавления
     * @return сохраненный пользователь с заполненным ID
     */
    @Override
    public User addUser(final User user) {
        String sqlQuery = "INSERT INTO users (email, login, name, birthday) "
                + "VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(
                    sqlQuery, new String[]{"id"});

            final int nameIdx = 3;
            final int birthdayIdx = 4;

            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(nameIdx, user.getName());
            stmt.setDate(birthdayIdx, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);

        long generatedId = Objects.requireNonNull(keyHolder.getKey())
                .longValue();
        user.setId(generatedId);
        log.info("Пользователь успешно сохранен в БД с ID: {}", generatedId);
        return user;
    }

    /**
     * Обновляет данные существующего пользователя в базе данных.
     *
     * @param user объект пользователя с обновленными данными
     * @return обновленный объект пользователя
     */
    @Override
    public User update(final User user) {
        String sqlQuery = "UPDATE users SET email = ?, login = ?, name = ?, "
                + "birthday = ? WHERE id = ?";

        int rowsUpdated = jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId());

        if (rowsUpdated == 0) {
            log.error("Пользователь с ID {} не найден для обновления",
                    user.getId());
            throw new NotFoundException(
                    "Пользователь с указанным ID не найден"
            );
        }

        log.info("Данные пользователя с ID {} успешно обновлены в БД",
                user.getId());
        return user;
    }

    /**
     * Возвращает список всех пользователей из базы данных.
     *
     * @return коллекция всех зарегистрированных пользователей
     */
    @Override
    public Collection<User> getAllUsers() {
        String sqlQuery = "SELECT id, email, login, name, birthday FROM users";
        return jdbcTemplate.query(sqlQuery, userMapper::mapRowToUser);
    }

    /**
     * Добавляет связь дружбы между двумя пользователями.
     *
     * @param userId уникальный идентификатор инициатора дружбы
     * @param friendId уникальный идентификатор добавляемого друга
     */
    @Override
    public void addFriend(final Long userId, final Long friendId) {
        String checkSql = "SELECT COUNT(*) FROM user_friends "
                + "WHERE user_id = ? AND friend_id = ?";
        Integer count = jdbcTemplate.queryForObject(
                checkSql, Integer.class, userId, friendId);

        if (count != null && count > 0) {
            log.info("Заявка от пользователя {} к {} уже существует",
                    userId, friendId);
            return;
        }

        String checkReverseSql = "SELECT COUNT(*) FROM user_friends "
                + "WHERE user_id = ? AND friend_id = ?";
        Integer hasReverse = jdbcTemplate.queryForObject(
                checkReverseSql, Integer.class, friendId, userId);

        if (hasReverse != null && hasReverse > 0) {
            String insertSql = "INSERT INTO user_friends (user_id, friend_id, "
                    + "status_id) VALUES (?, ?, 2)";
            jdbcTemplate.update(insertSql, userId, friendId);

            String updateSql = "UPDATE user_friends SET status_id = 2 "
                    + "WHERE user_id = ? AND friend_id = ?";
            jdbcTemplate.update(updateSql, friendId, userId);
            log.info("Дружба между пользователями {} и {} успешно "
                    + "подтверждена (CONFIRMED)", userId, friendId);
        } else {
            String insertSql = "INSERT INTO user_friends (user_id, friend_id, "
                    + "status_id) VALUES (?, ?, 1)";
            jdbcTemplate.update(insertSql, userId, friendId);
            log.info("Пользователь {} отправил запрос в друзья "
                    + "пользователю {} (UNCONFIRMED)", userId, friendId);
        }
    }

    /**
     * Удаляет связь дружбы между двумя пользователями.
     *
     * @param userId уникальный идентификатор инициатора удаления
     * @param friendId уникальный идентификатор удаляемого друга
     */
    @Override
    public void removeFriend(final Long userId, final Long friendId) {
        String deleteSql = "DELETE FROM user_friends "
                + "WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(deleteSql, userId, friendId);

        String updateReverseSql = "UPDATE user_friends SET status_id = 1 "
                + "WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(updateReverseSql, friendId, userId);

        log.info("Пользователь {} удалил из друзей пользователя {}",
                userId, friendId);
    }

    /**
     * Возвращает список всех друзей пользователя.
     *
     * @param userId уникальный идентификатор пользователя
     * @return коллекция моделей друзей пользователя
     */
    @Override
    public Collection<User> getFriends(final Long userId) {
        String sql = "SELECT u.* FROM users u "
                + "JOIN user_friends uf ON u.id = uf.friend_id "
                + "WHERE uf.user_id = ?";
        return jdbcTemplate.query(sql, userMapper::mapRowToUser, userId);
    }

    /**
     * Возвращает список общих друзей двух пользователей.
     *
     * @param userId уникальный идентификатор первого пользователя
     * @param otherId уникальный идентификатор второго пользователя
     * @return коллекция моделей общих друзей
     */
    @Override
    public Collection<User> getCommonFriends(
            final Long userId,
            final Long otherId) {
        String sql = "SELECT u.* FROM users u "
                + "JOIN user_friends uf1 ON u.id = uf1.friend_id "
                + "JOIN user_friends uf2 ON u.id = uf2.friend_id "
                + "WHERE uf1.user_id = ? AND uf2.user_id = ?";
        return jdbcTemplate.query(
                sql, userMapper::mapRowToUser, userId, otherId);
    }

    /**
     * Находит пользователя по его уникальному идентификатору.
     *
     * @param id уникальный идентификатор пользователя
     * @return Optional, содержащий пользователя,
     * или empty, если строка не найдена
     */
    @Override
    public Optional<User> findUserById(final Long id) {
        String sqlQuery = "SELECT id, email, login, name, birthday "
                + "FROM users WHERE id = ?";
        try {
            User user = jdbcTemplate.queryForObject(
                    sqlQuery, userMapper::mapRowToUser, id);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Пользователь с ID {} не найден в базе данных", id);
            return Optional.empty();
        }
    }
}
