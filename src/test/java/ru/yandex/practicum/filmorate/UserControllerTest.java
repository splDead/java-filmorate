package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    private User createValidUser() {
        User user = new User();
        user.setEmail("test@yandex.ru");
        user.setLogin("nagibator99");
        user.setName("Иван");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        return user;
    }

    @Test
    void shouldCreateUserWhenUserIsValid() {
        User user = createValidUser();

        User savedUser = userController.addUser(user);

        assertNotNull(savedUser);
        assertEquals(1, savedUser.getId());
        assertEquals(1, userController.getAllUsers().size());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsEmpty() {
        User user = createValidUser();
        user.setEmail("   ");

        ValidationException ex = assertThrows(ValidationException.class, () -> {
            userController.addUser(user);
        });
        assertEquals("Email не может быть пустым", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenEmailDoesNotContainAtSign() {
        User user = createValidUser();
        user.setEmail("myaddress-yandex.ru");

        ValidationException ex = assertThrows(ValidationException.class, () -> {
            userController.addUser(user);
        });
        assertEquals("Email должен содержать символ @", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenLoginIsEmpty() {
        User user = createValidUser();
        user.setLogin("");

        ValidationException ex = assertThrows(ValidationException.class, () -> {
            userController.addUser(user);
        });
        assertEquals("Логин не может быть пустым или содержать пробелы", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenLoginContainsSpaces() {
        User user = createValidUser();
        user.setLogin("super user 77");

        ValidationException ex = assertThrows(ValidationException.class, () -> {
            userController.addUser(user);
        });
        assertEquals("Логин не может быть пустым или содержать пробелы", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenBirthdayIsInFuture() {
        User user = createValidUser();
        user.setBirthday(LocalDate.now().plusDays(1)); // Завтрашний день

        ValidationException ex = assertThrows(ValidationException.class, () -> {
            userController.addUser(user);
        });
        assertEquals("Дата рождения не может быть пустой или быть из будущего", ex.getMessage());
    }

    @Test
    void shouldSetLoginAsNameWhenNameIsEmpty() {
        User user = createValidUser();
        user.setName("");

        User savedUser = userController.addUser(user);

        assertEquals(user.getLogin(), savedUser.getName(), "Если имя пустое, оно должно заменяться на логин");
    }

    @Test
    void shouldUpdateUserWhenUserExistsAndValid() {
        User user = createValidUser();
        User savedUser = userController.addUser(user);

        User updatedData = createValidUser();
        updatedData.setId(savedUser.getId());
        updatedData.setName("Новое Имя");

        User result = userController.update(updatedData);

        assertEquals("Новое Имя", result.getName());
        assertEquals(savedUser.getId(), result.getId());
    }

    @Test
    void shouldReturnAllUsers() {
        assertTrue(userController.getAllUsers().isEmpty());

        userController.addUser(createValidUser());

        User secondUser = createValidUser();
        secondUser.setLogin("admin");
        secondUser.setEmail("admin@test.ru");
        userController.addUser(secondUser);

        Collection<User> allUsers = userController.getAllUsers();
        assertEquals(2, allUsers.size());
    }
}
