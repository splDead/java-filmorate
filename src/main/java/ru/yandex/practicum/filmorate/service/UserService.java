package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Сервис для обработки бизнес-логики, связанной с пользователями.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    /** Хранилище объектов пользователей. */
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    /** Маппер для преобразования объектов пользователей. */
    private final UserMapper userMapper;

    /** Шаблон JDBC для выполнения прямых запросов к БД при проверках. */
    private final JdbcTemplate jdbcTemplate;

    /**
     * Создает нового пользователя в системе.
     *
     * @param userDto DTO объект создаваемого пользователя
     * @return DTO объект сохраненного пользователя
     */
    public UserDto createUser(final UserDto userDto) {
        log.info("Получен запрос на создание пользователя: {}",
                userDto.getLogin());

        User user = userMapper.toModel(userDto);
        User savedUser = userStorage.addUser(user);

        return userMapper.toDto(savedUser);
    }

    /**
     * Обновляет данные существующего пользователя.
     *
     * @param userDto DTO объект с обновленными данными
     * @return DTO объект обновленного пользователя
     */
    public UserDto updateUser(final UserDto userDto) {
        log.info("Получен запрос на обновление пользователя с ID: {}",
                userDto.getId());
        if (userDto.getId() == null) {
            throw new IllegalArgumentException(
                    "ID пользователя не может быть пустым при обновлении");
        }

        User user = userMapper.toModel(userDto);
        User updatedUser = userStorage.update(user);

        return userMapper.toDto(updatedUser);
    }

    /**
     * Возвращает коллекцию всех доступных пользователей.
     *
     * @return коллекция DTO всех пользователей
     */
    public Collection<UserDto> getAllUsers() {
        log.info("Получен запрос на получение всех пользователей");
        return userStorage.getAllUsers().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Добавляет пользователя в список друзей.
     *
     * @param userId уникальный идентификатор инициатора
     * @param friendId уникальный идентификатор добавляемого друга
     */
    public void addFriend(final Long userId, final Long friendId) {
        log.info("Запрос на добавление в друзья: {} -> {}", userId, friendId);

        userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException(
                        "Пользователь с ID " + userId + " не найден"));
        userStorage.findUserById(friendId)
                .orElseThrow(() -> new NotFoundException(
                        "Пользователь с ID " + friendId + " не найден"));

        if (userId.equals(friendId)) {
            throw new IllegalArgumentException(
                    "Пользователь не может добавить в друзья самого себя");
        }

        userStorage.addFriend(userId, friendId);
    }

    /**
     * Удаляет пользователя из списка друзей.
     *
     * @param userId уникальный идентификатор инициатора
     * @param friendId уникальный идентификатор удаляемого друга
     */
    public void removeFriend(final Long userId, final Long friendId) {
        log.info("Запрос на удаление из друзей: {} -> {}", userId, friendId);
        checkUserExists(userId);
        checkUserExists(friendId);

        userStorage.removeFriend(userId, friendId);
    }

    /**
     * Возвращает список всех друзей конкретного пользователя.
     *
     * @param userId уникальный идентификатор пользователя
     * @return коллекция DTO объектов друзей
     */
    public Collection<UserDto> getFriends(final Long userId) {
        log.info("Получение списка друзей для пользователя с ID: {}", userId);
        checkUserExists(userId);

        return userStorage.getFriends(userId).stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Возвращает список общих друзей двух пользователей.
     *
     * @param userId уникальный идентификатор первого пользователя
     * @param otherId уникальный идентификатор второго пользователя
     * @return коллекция DTO объектов общих друзей
     */
    public Collection<UserDto> getCommonFriends(
            final Long userId,
            final Long otherId) {
        log.info("Получение общих друзей для пользователей: {} и {}",
                userId, otherId);
        checkUserExists(userId);
        checkUserExists(otherId);

        return userStorage.getCommonFriends(userId, otherId).stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Проверяет физическое существование пользователя в базе данных.
     *
     * @param id уникальный идентификатор пользователя
     */
    private void checkUserExists(final Long id) {
        String sql = "SELECT COUNT(*) FROM users WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        if (count == null || count == 0) {
            throw new NotFoundException("Пользователь с ID "
                    + id + " не найден");
        }
    }

    /**
     * Находит пользователя по его уникальному идентификатору.
     *
     * @param id уникальный идентификатор пользователя
     * @return DTO объект найденного пользователя
     */
    public UserDto findUserById(final Long id) {
        log.info("Получен запрос на поиск пользователя по ID: {}", id);

        User user = userStorage.findUserById(id)
                .orElseThrow(() -> {
                    log.error("Пользователь с ID {} не найден", id);
                    return new NotFoundException(
                            "Пользователь с ID " + id + " не найден");
                });

        return userMapper.toDto(user);
    }
}
