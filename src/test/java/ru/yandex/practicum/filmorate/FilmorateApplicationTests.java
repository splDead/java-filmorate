package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@ComponentScan(basePackages = "ru.yandex.practicum.filmorate")
class FilmoRateApplicationTests {
	@Autowired
	@Qualifier("userDbStorage")
	private UserStorage userStorage;

	@Autowired
	@Qualifier("filmDbStorage")
	private FilmStorage filmStorage;

	@Test
	public void testFindUserById() {
		User testUser = User.builder()
				.email("integration@test.ru")
				.login("test_user")
				.name("Тестовый Пользователь")
				.birthday(LocalDate.of(1995, 5, 20))
				.build();

		User savedUser = userStorage.addUser(testUser);
		assertNotNull(savedUser.getId(), "База данных должна сгенерировать ID для пользователя");

		Optional<User> userOptional = userStorage.findUserById(savedUser.getId());

		assertTrue(userOptional.isPresent(), "Пользователь должен быть найден по ID");

		User retrievedUser = userOptional.get();
		assertEquals(savedUser.getId(), retrievedUser.getId());
		assertEquals("test_user", retrievedUser.getLogin());
		assertEquals("integration@test.ru", retrievedUser.getEmail());
	}
}
