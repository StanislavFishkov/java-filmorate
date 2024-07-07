package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    public User create(User user) {
        checkUserConstraints(user);

        User createdUser = userStorage.create(user);
        log.info("User is created: {}", createdUser);
        return createdUser;
    }

    public User update(User newUser) {
        if (newUser.getId() == null) {
            throw new ValidationException("User id can't be null on update: " + newUser, log);
        }

        if (userStorage.get(newUser.getId()) != null) {
            checkUserConstraints(newUser);

            User updatedUser = userStorage.update(newUser);
            log.info("User is updated: {}", updatedUser);
            return updatedUser;
        }
        throw new ValidationException("User can't be found by id: " + newUser, log);
    }

    private void checkUserConstraints(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("User email can't be empty and should contain @: " + user, log);
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("User login can't be empty or contain spaces: " + user, log);
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("User birthday can't be empty or in the future: " + user, log);
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}