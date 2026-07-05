package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.exeption.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.IdGenerator;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Контроллер для обработки запросов, связанных с пользователями.
 */
@Slf4j
@RestController
@RequestMapping("/users")
public final class UserController {

    /** Хранилище пользователей. */
    private final Map<Long, User> users = new ConcurrentHashMap<>();

    /**
     * Добавляет нового пользователя в систему.
     *
     * @param user объект пользователя для добавления
     * @return сохраненный пользователь с присвоенным ID
     */
    @PostMapping
    public User addUser(@Valid @RequestBody final User user) {
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

    /**
     * Обновляет данные существующего пользователя.
     *
     * @param newUser объект пользователя с обновленными данными
     * @return обновленный пользователь
     */
    @PutMapping
    public User update(@Valid @RequestBody final User newUser) {
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
        log.info("Информация о пользователе с id = {} обновлена",
                newUser.getId());

        return newUser;
    }

    /**
     * Возвращает список всех сохраненных пользователей.
     *
     * @return коллекция всех пользователей
     */
    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Получен запрос на список всех пользователей. Всего: {}",
                users.size());
        return users.values();
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
