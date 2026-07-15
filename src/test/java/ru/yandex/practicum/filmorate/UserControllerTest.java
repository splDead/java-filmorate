package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController(new UserService(new InMemoryUserStorage()));
    }

    private User createValidUser() {
        return User.builder()
                .email("test@yandex.ru")
                .login("nagibator99")
                .name("Иван")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
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
