package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@Valid @RequestBody NewUserRequest userRequest) {
        return userService.createUser(userRequest);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable("id") Long userId,
                              @Valid @RequestBody UpdateUserRequest userRequest) {
        return userService.updateUser(userId, userRequest);
    }

    @GetMapping
    public Collection<UserDto> findAllUsers() {
        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto findUserById(@PathVariable Long id) {
        return userService.findUserById(id);
    }

    @DeleteMapping("/{id}")
    public boolean deleteUser(@PathVariable("id") Long userId) {
        return userService.deleteUser(userId);
    }
}
