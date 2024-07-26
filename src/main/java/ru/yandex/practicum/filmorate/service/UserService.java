package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;

import java.time.LocalDate;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Collection<User> getAll() {
        log.trace("List of all users is requested");
        return userRepository.getAll();
    }

    public User get(long userId) {
        if (!userRepository.checkUserExists(userId)) {
            throw new NotFoundException("User can't be found on getting by id: " + userId);
        }

        log.trace("User is requested by id: {}", userId);
        return userRepository.get(userId);
    }

    public User create(User user) {
        checkUserConstraints(user);

        User createdUser = userRepository.create(user);
        log.info("User was created: {}", createdUser);
        return createdUser;
    }

    public User update(User newUser) {
        if (newUser.getId() == null) {
            throw new ValidationException("User id can't be null on update: " + newUser);
        }
        if (!userRepository.checkUserExists(newUser.getId())) {
            throw new NotFoundException("User can't be found on update by id: " + newUser);
        }

        checkUserConstraints(newUser);

        User updatedUser = userRepository.update(newUser);
        log.info("User was updated: {}", updatedUser);
        return updatedUser;
    }

    public void addFriend(Long userId, Long friendId) {
        if (!userRepository.checkUserExists(userId)) {
            throw new NotFoundException("User can't be found on adding friend by id: " + userId);
        }
        if (!userRepository.checkUserExists(friendId)) {
            throw new NotFoundException("Friend can't be found on adding friend by id: " + friendId);
        }

        userRepository.addFriend(userId, friendId);
        log.info("User with id {} added a friend with id {}", userId, friendId);
    }

    public Collection<User> getFriends(Long userId) {
        if (!userRepository.checkUserExists(userId)) {
            throw new NotFoundException("User can't be found on getting friends by id: " + userId);
        }
        log.trace("List of all friends is requested with user id: {}", userId);
        return userRepository.getFriends(userId);
    }

    public Collection<User> getMutualFriends(Long firstUserId, Long secondUserId) {
        if (!userRepository.checkUserExists(firstUserId)) {
            throw new NotFoundException("User can't be found on getting mutual friends by id: " + firstUserId);
        }
        if (!userRepository.checkUserExists(secondUserId)) {
            throw new NotFoundException("User can't be found on getting mutual friends by id: " + secondUserId);
        }

        log.trace("List of all mutual friends is requested with user ids: {}, {}", firstUserId, secondUserId);
        return userRepository.getMutualFriends(firstUserId, secondUserId);
    }

    public void removeFriend(Long userId, Long friendId) {
        if (!userRepository.checkUserExists(userId)) {
            throw new NotFoundException("User can't be found on removing friend by id: " + userId);
        }
        if (!userRepository.checkUserExists(friendId)) {
            throw new NotFoundException("Friend can't be found on removing friend by id: " + friendId);
        }

        userRepository.removeFriend(userId, friendId);
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