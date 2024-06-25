package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private Map<Integer, User> users = new HashMap<>();
    protected Integer idCounter = 0;

    @GetMapping
    public Collection<User> getAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        checkUserConstraints(user);

        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("User is created: {}", user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        if (newUser.getId() == null) {
            throw new ValidationException("User id can't be null on update: " + newUser, log);
        }

        if (users.containsKey(newUser.getId())) {
            checkUserConstraints(newUser);

            users.put(newUser.getId(), newUser);
            log.info("User is updated: {}", newUser);
            return newUser;
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

    private int getNextId() {
        return ++idCounter;
    }
}
