package ru.yandex.practicum.filmorate.repository.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserRepository {
    boolean checkUserExists(Long userId);

    boolean checkUserExistsByEmail(User user);

    User create(User user);

    User get(Long userId);

    Collection<User> getAll();

    User update(User user);

    void delete(Long userId);

    void addFriend(Long userId, Long friendId);

    Collection<User> getFriends(Long userId);

    Collection<User> getMutualFriends(Long firstUserId, Long secondUserId);

    void removeFriend(Long userId, Long friendId);
}