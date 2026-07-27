package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserControllerTest {

    private UserController userController;
    private UserService userService;

    @BeforeEach
    void setUp() {
        // Создаем мок для сервиса пользователей
        userService = Mockito.mock(UserService.class);
        userController = new UserController(userService);
    }

    private UserDto createValidUserDto() {
        return UserDto.builder()
                .email("test@yandex.ru")
                .login("nagibator99")
                .name("Иван")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
    }

    @Test
    void shouldCreateUserWhenUserIsValid() {
        UserDto inputDto = createValidUserDto();
        UserDto savedDto = createValidUserDto();
        savedDto.setId(1L);

        when(userService.createUser(any(UserDto.class))).thenReturn(savedDto);
        when(userService.getAllUsers()).thenReturn(List.of(savedDto));

        UserDto result = userController.createUser(inputDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1, userController.getAllUsers().size());
    }

    @Test
    void shouldSetLoginAsNameWhenNameIsEmpty() {
        UserDto inputDto = createValidUserDto();
        inputDto.setName("");

        UserDto savedDto = createValidUserDto();
        savedDto.setId(1L);
        savedDto.setName(inputDto.getLogin()); // Ожидаем, что сервис вернет логин вместо имени

        when(userService.createUser(any(UserDto.class))).thenReturn(savedDto);

        UserDto result = userController.createUser(inputDto);

        assertEquals(inputDto.getLogin(), result.getName(), "Если имя пустое, оно должно заменяться на логин");
    }

    @Test
    void shouldUpdateUserWhenUserExistsAndValid() {
        UserDto updatedData = createValidUserDto();
        updatedData.setId(1L);
        updatedData.setName("Новое Имя");

        when(userService.updateUser(any(UserDto.class))).thenReturn(updatedData);

        UserDto result = userController.updateUser(updatedData);

        assertEquals("Новое Имя", result.getName());
        assertEquals(1L, result.getId());
    }

    @Test
    void shouldReturnAllUsers() {
        UserDto firstUser = createValidUserDto();
        firstUser.setId(1L);

        UserDto secondUser = createValidUserDto();
        secondUser.setId(2L);
        secondUser.setLogin("admin");
        secondUser.setEmail("admin@test.ru");

        when(userService.getAllUsers()).thenReturn(List.of(firstUser, secondUser));

        Collection<UserDto> allUsers = userController.getAllUsers();
        assertEquals(2, allUsers.size());
    }

    @Test
    void shouldReturnUserById() {
        UserDto userDto = createValidUserDto();
        userDto.setId(1L);

        when(userService.findUserById(1L)).thenReturn(userDto);

        UserDto result = userController.findUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("nagibator99", result.getLogin());
    }

    @Test
    void shouldCallServiceToAddFriend() {
        doNothing().when(userService).addFriend(anyLong(), anyLong());

        userController.addFriend(1L, 2L);

        verify(userService, times(1)).addFriend(1L, 2L);
    }

    @Test
    void shouldCallServiceToRemoveFriend() {
        doNothing().when(userService).removeFriend(anyLong(), anyLong());

        userController.removeFriend(1L, 2L);

        verify(userService, times(1)).removeFriend(1L, 2L);
    }

    @Test
    void shouldReturnUserFriendsList() {
        UserDto friend = createValidUserDto();
        friend.setId(2L);
        friend.setLogin("friend_user");

        when(userService.getFriends(1L)).thenReturn(List.of(friend));

        Collection<UserDto> result = userController.getFriends(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("friend_user", result.iterator().next().getLogin());
        verify(userService, times(1)).getFriends(1L);
    }

    @Test
    void shouldReturnCommonFriendsList() {
        UserDto commonFriend = createValidUserDto();
        commonFriend.setId(3L);
        commonFriend.setLogin("common_friend");

        when(userService.getCommonFriends(1L, 2L)).thenReturn(List.of(commonFriend));

        Collection<UserDto> result = userController.getCommonFriends(1L, 2L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("common_friend", result.iterator().next().getLogin());
        verify(userService, times(1)).getCommonFriends(1L, 2L);
    }
}
