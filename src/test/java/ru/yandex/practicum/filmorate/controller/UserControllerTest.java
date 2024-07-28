package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.repository.user.InMemoryUserRepository;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    UserController userController;
    User verifiedUser;

    @BeforeEach
    void setUp() {
        userController = new UserController(new UserService(new InMemoryUserRepository()));
        verifiedUser = User.builder()
                .id(1L)
                .email("email@domen.com")
                .login("Login")
                .birthday(LocalDate.of(1984, 12, 26))
                .name("Name")
                .build();
    }

    @Test
    void createUser() {
        userController.create(verifiedUser);
        assertEquals(1, userController.getAll().size());
        assertEquals(verifiedUser.getName(), userController.getAll().stream().findFirst().get().getName());
    }

    @Test
    void updateFilm() {
        userController.create(verifiedUser);
        userController.update(verifiedUser.toBuilder().login("Different").build());

        assertEquals(1, userController.getAll().size());
        assertNotEquals(verifiedUser.getLogin(), userController.getAll().stream().findFirst().get().getLogin());
    }

    @Test
    void emailNotEmptyContainsAt() {
        User userBlankEmail = verifiedUser.toBuilder().email(" ").build();
        assertThrows(ValidationException.class, () -> userController.create(userBlankEmail));

        User user = verifiedUser.toBuilder().email("email$domain.com").build();
        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void loginNotEmptyWithoutSpaces() {
        User user = verifiedUser.toBuilder().login("test test").build();
        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void nameCanBeEmpty() {
        User user = verifiedUser.toBuilder().name(null).build();
        assertDoesNotThrow(() -> userController.create(user));

        User userFromController = userController.getAll().stream().findFirst().get();
        assertEquals(verifiedUser.getLogin(), userFromController.getName());
    }

    @Test
    void birthdayNotInTheFuture() {
        User user = verifiedUser.toBuilder().birthday(LocalDate.of(LocalDate.now().getYear() + 1, 1, 1)).build();
        assertThrows(ValidationException.class, () -> userController.create(user));
    }
}