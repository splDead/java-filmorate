package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

/**
 * Контроллер для обработки запросов, связанных с пользователями.
 */
@RestController
@RequestMapping("/users")
public final class UserController {

    /** Сервис для работы с пользователями. */
    private final UserService service;

    /**
     * Конструктор контроллера.
     *
     * @param userService сервис пользователей
     */
    public UserController(final UserService userService) {
        this.service = userService;
    }

    /**
     * Добавляет нового пользователя в систему.
     *
     * @param user объект пользователя для добавления
     * @return сохраненный пользователь с присвоенным ID
     */
    @PostMapping
    public User addUser(@Valid @RequestBody final User user) {
        return service.addUser(user);
    }

    /**
     * Обновляет данные существующего пользователя.
     *
     * @param newUser объект пользователя с обновленными данными
     * @return обновленный пользователь
     */
    @PutMapping
    public User update(@Valid @RequestBody final User newUser) {
        return service.update(newUser);
    }

    /**
     * Возвращает список всех сохраненных пользователей.
     *
     * @return коллекция всех пользователей
     */
    @GetMapping
    public Collection<User> getAllUsers() {
        return service.getAllUsers();
    }

    /**
     * Добавляет пользователя в список друзей.
     *
     * @param id       идентификатор пользователя
     * @param friendId идентификатор друга
     */
    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(
            @PathVariable final Long id,
            @PathVariable final Long friendId
    ) {
        service.addFriend(id, friendId);
    }

    /**
     * Удаляет пользователя из списка друзей.
     *
     * @param id       идентификатор пользователя
     * @param friendId идентификатор друга
     */
    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(
            @PathVariable final Long id,
            @PathVariable final Long friendId
    ) {
        service.removeFriend(id, friendId);
    }

    /**
     * Возвращает список друзей пользователя.
     *
     * @param id идентификатор пользователя
     * @return список друзей
     */
    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable final Long id) {
        return service.getFriends(id);
    }

    /**
     * Возвращает список общих друзей.
     *
     * @param id       идентификатор пользователя
     * @param friendId идентификатор другого пользователя
     * @return список общих друзей
     */
    @GetMapping("/{id}/friends/common/{friendId}")
    public List<User> getCommonFriends(
            @PathVariable final Long id,
            @PathVariable final Long friendId
    ) {
        return service.getCommonFriends(id, friendId);
    }
}
