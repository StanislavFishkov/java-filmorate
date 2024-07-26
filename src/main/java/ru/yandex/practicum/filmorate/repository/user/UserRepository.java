package ru.yandex.practicum.filmorate.repository.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserRepository {
    User create(User user);

    boolean checkUserExists(Long userId);

    User get(Long userId);

    Collection<User> getAll();

    User update(User user);

    void delete(Long userId);

    void addFriend(Long userId, Long friendId);

    Collection<User> getFriends(Long userId);

    Collection<User> getMutualFriends(Long firstUserId, Long secondUserId);

    void removeFriend(Long userId, Long friendId);
}