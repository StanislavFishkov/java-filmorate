package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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
        log.trace("List of all users is requested");
        return userStorage.getAll();
    }

    public User get(long userId) {
        if (!userStorage.checkUserExists(userId)) {
            throw new NotFoundException("User can't be found on getting by id: " + userId);
        }

        log.trace("User is requested by id: {}", userId);
        return userStorage.get(userId);
    }

    public User create(User user) {
        checkUserConstraints(user);

        User createdUser = userStorage.create(user);
        log.info("User was created: {}", createdUser);
        return createdUser;
    }

    public User update(User newUser) {
        if (newUser.getId() == null) {
            throw new ValidationException("User id can't be null on update: " + newUser);
        }
        if (!userStorage.checkUserExists(newUser.getId())) {
            throw new NotFoundException("User can't be found on update by id: " + newUser);
        }

        checkUserConstraints(newUser);

        User updatedUser = userStorage.update(newUser);
        log.info("User was updated: {}", updatedUser);
        return updatedUser;
    }

    public void addFriend(Long userId, Long friendId) {
        if (!userStorage.checkUserExists(userId)) {
            throw new NotFoundException("User can't be found on adding friend by id: " + userId);
        }
        if (!userStorage.checkUserExists(friendId)) {
            throw new NotFoundException("Friend can't be found on adding friend by id: " + friendId);
        }

        userStorage.addFriend(userId, friendId);
        log.info("User with id {} added a friend with id {}", userId, friendId);
    }

    public Collection<User> getFriends(Long userId) {
        if (!userStorage.checkUserExists(userId)) {
            throw new NotFoundException("User can't be found on getting friends by id: " + userId);
        }
        log.trace("List of all friends is requested with user id: {}", userId);
        return userStorage.getFriends(userId);
    }

    public Collection<User> getMutualFriends(Long firstUserId, Long secondUserId) {
        if (!userStorage.checkUserExists(firstUserId)) {
            throw new NotFoundException("User can't be found on getting mutual friends by id: " + firstUserId);
        }
        if (!userStorage.checkUserExists(secondUserId)) {
            throw new NotFoundException("User can't be found on getting mutual friends by id: " + secondUserId);
        }

        log.trace("List of all mutual friends is requested with user ids: {}, {}", firstUserId, secondUserId);
        return userStorage.getMutualFriends(firstUserId, secondUserId);
    }

    public void removeFriend(Long userId, Long friendId) {
        if (!userStorage.checkUserExists(userId)) {
            throw new NotFoundException("User can't be found on removing friend by id: " + userId);
        }
        if (!userStorage.checkUserExists(friendId)) {
            throw new NotFoundException("Friend can't be found on removing friend by id: " + friendId);
        }

        userStorage.removeFriend(userId, friendId);
        log.info("User with id {} removed a friend with id {}", userId, friendId);
    }

    private void checkUserConstraints(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("User email can't be empty and should contain @: " + user);
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("User login can't be empty or contain spaces: " + user);
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("User birthday can't be empty or in the future: " + user);
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}