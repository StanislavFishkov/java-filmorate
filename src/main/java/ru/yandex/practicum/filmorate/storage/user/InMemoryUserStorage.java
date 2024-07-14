package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    protected Long idCounter = 0L;

    @Override
    public User create(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public boolean checkUserExists(Long userId) {
        return users.containsKey(userId);
    }

    @Override
    public User get(Long userId) {
        return users.get(userId);
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(Long userId) {
        users.remove(userId);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        users.get(userId).getFriends().add(friendId);
        users.get(friendId).getFriends().add(userId);
    }

    @Override
    public Collection<User> getFriends(Long userId) {
        return users.get(userId).getFriends().stream()
                .map(users::get)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<User> getMutualFriends(Long firstUserId, Long secondUserId) {
        final Set<Long> secondUserFriends = users.get(secondUserId).getFriends();
        return users.get(firstUserId).getFriends().stream()
                .filter(secondUserFriends::contains)
                .map(users::get)
                .collect(Collectors.toList());
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        users.get(userId).getFriends().remove(friendId);
        users.get(friendId).getFriends().remove(userId);
    }

    private long getNextId() {
        return ++idCounter;
    }
}