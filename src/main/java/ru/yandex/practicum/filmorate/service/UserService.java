package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Сервисный класс для управления операциями с пользователями.
 */
@Service
public final class UserService {

    /** Хранилище пользователей. */
    private final InMemoryUserStorage storage;

    /**
     * Конструктор сервиса.
     *
     * @param userStorage хранилище пользователей
     */
    public UserService(final InMemoryUserStorage userStorage) {
        this.storage = userStorage;
    }

    /**
     * Добавляет нового пользователя в систему.
     *
     * @param user объект пользователя для добавления
     * @return сохраненный пользователь с присвоенным ID
     */
    public User addUser(final User user) {
        return storage.addUser(user);
    }

    /**
     * Обновляет данные существующего пользователя.
     *
     * @param newUser объект пользователя с обновленными данными
     * @return обновленный пользователь
     */
    public User update(final User newUser) {
        return storage.update(newUser);
    }

    /**
     * Возвращает список всех сохраненных пользователей.
     *
     * @return коллекция всех пользователей
     */
    public Collection<User> getAllUsers() {
        return storage.getAllUsers();
    }

    /**
     * Добавляет пользователя в список друзей другого пользователя.
     *
     * @param userId   идентификатор инициатора дружбы
     * @param friendId идентификатор добавляемого друга
     */
    public void addFriend(final Long userId, final Long friendId) {
        User user = storage.findById(userId);
        User friend = storage.findById(friendId);

        if (user == null || friend == null) {
            throw new NotFoundException("Пользователь не найден");
        }

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    /**
     * Удаляет пользователя из списка друзей другого пользователя.
     *
     * @param userId   идентификатор инициатора удаления
     * @param friendId идентификатор удаляемого друга
     */
    public void removeFriend(final Long userId, final Long friendId) {
        User user = storage.findById(userId);
        User friend = storage.findById(friendId);

        if (user == null) {
            throw new NotFoundException("Пользователь с id=" + userId
                    + " не найден");
        }
        if (friend == null) {
            throw new NotFoundException("Пользователь (друг) с id=" + friendId
                    + " не найден");
        }

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    /**
     * Возвращает список друзей конкретного пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список друзей пользователя
     */
    public List<User> getFriends(final Long userId) {
        User user = storage.findById(userId);

        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }

        Set<Long> friendIds = new HashSet<>(user.getFriends());

        return friendIds.stream()
                .map(storage::findById)
                .collect(Collectors.toList());
    }

    /**
     * Возвращает список общих друзей двух пользователей.
     *
     * @param userId   идентификатор первого пользователя
     * @param friendId идентификатор второго пользователя
     * @return список общих друзей
     */
    public List<User> getCommonFriends(final Long userId, final Long friendId) {
        User user = storage.findById(userId);
        User friend = storage.findById(friendId);

        if (user == null || friend == null) {
            throw new NotFoundException("Пользователь не найден");
        }

        Set<Long> commonFriendIds = new HashSet<>(user.getFriends());
        commonFriendIds.retainAll(friend.getFriends());

        return commonFriendIds.stream()
                .map(storage::findById)
                .collect(Collectors.toList());
    }
}
