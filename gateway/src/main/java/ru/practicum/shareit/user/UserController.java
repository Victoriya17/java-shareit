package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody NewUserRequest userRequest) {
        return userClient.createUser(userRequest);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable("id") Long userId,
                                             @Valid @RequestBody UpdateUserRequest userRequest) {
        return userClient.updateUser(userId, userRequest);
    }

    @GetMapping
    public ResponseEntity<Object> findAllUsers() {
        return userClient.findAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findUserById(@PathVariable Long id) {
        return userClient.findUserById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable("id") Long userId) {
        return userClient.deleteUser(userId);
    }
}
