package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

/**
 * Интерфейс для управления хранилищем пользователей.
 */
public interface UserStorage {

    /**
     * Добавляет нового пользователя в хранилище.
     *
     * @param user объект пользователя для добавления
     * @return сохраненный пользователь с присвоенным идентификатором
     */
    User addUser(User user);

    /**
     * Обновляет данные существующего пользователя в хранилище.
     *
     * @param user объект пользователя с обновленными данными
     * @return обновленный пользователь
     */
    User update(User user);

    /**
     * Возвращает коллекцию всех сохраненных пользователей.
     *
     * @return коллекция всех пользователей в хранилище
     */
    Collection<User> getAllUsers();

    /**
     * Добавляет пользователя в друзья.
     *
     * @param userId уникальный идентификатор инициатора дружбы
     * @param friendId уникальный идентификатор добавляемого друга
     */
    void addFriend(Long userId, Long friendId);

    /**
     * Удаляет пользователя из друзей.
     *
     * @param userId уникальный идентификатор инициатора удаления
     * @param friendId уникальный идентификатор удаляемого друга
     */
    void removeFriend(Long userId, Long friendId);

    /**
     * Возвращает список всех друзей пользователя.
     *
     * @param userId уникальный идентификатор пользователя
     * @return коллекция доменных моделей друзей пользователя
     */
    Collection<User> getFriends(Long userId);

    /**
     * Возвращает список общих друзей двух пользователей.
     *
     * @param userId уникальный идентификатор первого пользователя
     * @param otherId уникальный идентификатор второго пользователя
     * @return коллекция доменных моделей общих друзей
     */
    Collection<User> getCommonFriends(Long userId, Long otherId);

    /**
     * Находит пользователя по его уникальному идентификатору.
     *
     * @param id уникальный идентификатор пользователя
     * @return Optional, содержащий найденного пользователя, или empty
     */
    Optional<User> findUserById(Long id);
}
