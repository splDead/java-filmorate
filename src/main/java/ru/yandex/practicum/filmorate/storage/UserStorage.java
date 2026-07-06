package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

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
}
