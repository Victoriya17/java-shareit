package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController controller;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
        userDto = new UserDto(1L, "John Doe", "john.doe@mail.com");
    }

    @Test
    void createUserTest() throws Exception {
        NewUserRequest request = new NewUserRequest();
        request.setName("John Doe");
        request.setEmail("john.doe@mail.com");

        when(userService.createUser(any())).thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    void updateUserTest() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("Updated Name");

        UserDto updatedUser = new UserDto(1L, "Updated Name", "john.doe@mail.com");

        when(userService.updateUser(anyLong(), any())).thenReturn(updatedUser);

        mvc.perform(patch("/users/" + userDto.getId())
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("john.doe@mail.com"));
    }

    @Test
    void findUserByIdTest() throws Exception {
        when(userService.findUserById(anyLong())).thenReturn(userDto);

        mvc.perform(get("/users/" + userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()));
    }

    @Test
    void findAllUsersTest() throws Exception {
        when(userService.findAllUsers()).thenReturn(List.of(userDto));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(userDto.getId()));
    }

    @Test
    void deleteUserTest() throws Exception {
        mvc.perform(delete("/users/" + userDto.getId()))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(1L);
    }
}
