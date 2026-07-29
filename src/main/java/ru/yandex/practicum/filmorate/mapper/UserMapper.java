package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Маппер для преобразования объектов пользователей между слоями DTO,
 * моделей и БД.
 */
@Component
public final class UserMapper {

    /**
     * Преобразует объект UserDto в доменную модель User.
     * Если имя пустое или состоит из пробелов, вместо него используется логин.
     *
     * @param dto входящий объект переноса данных
     * @return доменная модель пользователя
     */
    public User toModel(final UserDto dto) {
        if (dto == null) {
            return null;
        }

        // Разбиваем длинную строку на две аккуратные строчки
        boolean isNameEmpty = dto.getName() == null || dto.getName().isBlank();
        String finalName = isNameEmpty ? dto.getLogin() : dto.getName();

        return User.builder()
                .id(dto.getId())
                .email(dto.getEmail())
                .login(dto.getLogin())
                .name(finalName)
                .birthday(dto.getBirthday())
                .build();
    }

    /**
     * Преобразует доменную модель User в объект UserDto.
     *
     * @param user доменная модель пользователя
     * @return объект переноса данных пользователя
     */
    public UserDto toDto(final User user) {
        if (user == null) {
            return null;
        }
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .login(user.getLogin())
                .name(user.getName())
                .birthday(user.getBirthday())
                .build();
    }

    /**
     * Маппит строку результата SQL-запроса в доменную модель пользователя User.
     *
     * @param rs результат выборки из базы данных
     * @param rowNum номер текущей строки
     * @return собранный объект модели пользователя
     * @throws SQLException при ошибках чтения данных из ResultSet
     */
    public User mapRowToUser(final ResultSet rs, final int rowNum)
            throws SQLException {
        return User.builder()
                .id(rs.getLong("id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }
}
