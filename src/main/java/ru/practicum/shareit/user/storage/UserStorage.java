package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    User createUser(User user);

    User updateUser(User user);

    Collection<User> findAllUsers();

    Optional<User> findUserById(Long userId);

    boolean deleteUser(Long userId);

    Optional<User> findUserByEmail(String email);
}
