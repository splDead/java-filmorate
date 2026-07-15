package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.IdGenerator;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Хранилище пользователей в оперативной памяти.
 */
@Slf4j
@Component
public final class InMemoryUserStorage implements UserStorage {

    /** Хранилище пользователей. */
    private final Map<Long, User> users = new ConcurrentHashMap<>();

    @Override
    public User addUser(final User user) {
        log.info("Получен запрос на добавление пользователя: {}",
                user.getName());

        checkDuplicateUser(user);
        prepareName(user);

        long newId = IdGenerator.getNextId(users);
        user.setId(newId);
        users.put(user.getId(), user);

        log.info("Пользователь успешно добавлен с id = {}", user.getId());

        return user;
    }

    @Override
    public User update(final User newUser) {
        log.info("Получен запрос на обновление пользователя: {}",
                newUser.getName());

        if (newUser.getId() == null) {
            log.warn("Попытка обновить пользователя без id");
            throw new ValidationException("Id должен быть указан");
        }

        if (!users.containsKey(newUser.getId())) {
            log.warn("Попытка обновить несуществующего юзера с id = {}",
                    newUser.getId());
            throw new NotFoundException("Пользователь с id = "
                    + newUser.getId() + " не найден");
        }

        checkDuplicateUserForUpdate(newUser);
        prepareName(newUser);

        users.put(newUser.getId(), newUser);
        log.info("Информация о главе пользователя с id = {} обновлена",
                newUser.getId());

        return newUser;
    }

    @Override
    public Collection<User> getAllUsers() {
        log.info("Получен запрос на список всех пользователей. Всего: {}",
                users.size());
        return users.values();
    }

    /**
     * Находит пользователя по его идентификатору.
     *
     * @param id идентификатор искомого пользователя
     * @return найденный пользователь или null, если пользователь не найден
     */
    public User findById(final Long id) {
        return users.get(id);
    }

    /**
     * Проверяет и подставляет логин, если имя пустое.
     *
     * @param user объект пользователя для проверки имени
     */
    private void prepareName(final User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Пустое имя заполнено логином: {}",
                    user.getLogin());
            user.setName(user.getLogin());
        }
    }

    /**
     * Проверяет уникальность данных пользователя перед сохранением.
     *
     * @param user объект пользователя для проверки
     */
    private void checkDuplicateUser(final User user) {
        if (users.values().stream().anyMatch(user::equals)) {
            log.warn("Попытка добавить дубликат пользователя");
            throw new DuplicatedDataException("Этот пользователь уже добавлен");
        }
    }

    /**
     * Проверяет уникальность данных пользователя при обновлении.
     *
     * @param newUser обновленный объект пользователя
     */
    private void checkDuplicateUserForUpdate(final User newUser) {
        boolean isDuplicate = users.values().stream()
                .filter(u -> !u.getId().equals(newUser.getId()))
                .anyMatch(newUser::equals);

        if (isDuplicate) {
            log.warn("Попытка обновить юзера чужими данными");
            throw new DuplicatedDataException("Пользователь с такими "
                    + "данными уже существует");
        }
    }
}
