package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

/**
 * REST-контроллер для обработки запросов, связанных с пользователями.
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public final class UserController {

    /** Сервис для работы с бизнес-логикой пользователей. */
    private final UserService service;

    /**
     * Создает нового пользователя в системе.
     *
     * @param userDto данные создаваемого пользователя
     * @return созданный пользователь с присвоенным ID
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody final UserDto userDto) {
        log.info("REST-запрос на создание пользователя: {}",
                userDto.getLogin());
        return service.createUser(userDto);
    }

    /**
     * Обновляет данные существующего пользователя.
     *
     * @param userDto обновленные данные пользователя
     * @return обновленный объект пользователя
     */
    @PutMapping
    public UserDto updateUser(@Valid @RequestBody final UserDto userDto) {
        log.info("REST-запрос на обновление пользователя с ID: {}",
                userDto.getId());
        return service.updateUser(userDto);
    }

    /**
     * Возвращает коллекцию всех зарегистрированных пользователей.
     *
     * @return коллекция всех пользователей
     */
    @GetMapping
    public Collection<UserDto> getAllUsers() {
        log.info("REST-запрос на получение всех пользователей");
        return service.getAllUsers();
    }

    /**
     * Добавляет пользователя в друзья.
     *
     * @param id уникальный идентификатор инициатора
     * @param friendId уникальный идентификатор добавляемого друга
     */
    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(
            @PathVariable final Long id,
            @PathVariable final Long friendId) {
        log.info("REST-запрос: пользователь {} добавляет в друзья {}",
                id, friendId);
        service.addFriend(id, friendId);
    }

    /**
     * Удаляет пользователя из друзей.
     *
     * @param id уникальный идентификатор инициатора
     * @param friendId уникальный идентификатор удаляемого друга
     */
    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(
            @PathVariable final Long id,
            @PathVariable final Long friendId) {
        log.info("REST-запрос: пользователь {} удаляет из друзей {}",
                id, friendId);
        service.removeFriend(id, friendId);
    }

    /**
     * Возвращает список всех друзей конкретного пользователя.
     *
     * @param id уникальный идентификатор пользователя
     * @return коллекция DTO объектов друзей
     */
    @GetMapping("/{id}/friends")
    public Collection<UserDto> getFriends(@PathVariable final Long id) {
        log.info("REST-запрос: получение друзей пользователя {}", id);
        return service.getFriends(id);
    }

    /**
     * Возвращает список общих друзей между двумя пользователями.
     *
     * @param id уникальный идентификатор первого пользователя
     * @param otherId уникальный идентификатор второго пользователя
     * @return коллекция DTO объектов общих друзей
     */
    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<UserDto> getCommonFriends(
            @PathVariable final Long id,
            @PathVariable final Long otherId) {
        log.info("REST-запрос: получение общих друзей для {} и {}",
                id, otherId);
        return service.getCommonFriends(id, otherId);
    }

    /**
     * Возвращает пользователя по его уникальному идентификатору.
     *
     * @param id уникальный идентификатор искомого пользователя
     * @return DTO объект найденного пользователя
     */
    @GetMapping("/{id}")
    public UserDto findUserById(@PathVariable final Long id) {
        log.info("REST-запрос на получение пользователя с ID: {}", id);
        return service.findUserById(id);
    }
}
