package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplIntegrationTest {

    private final EntityManager em;
    private final UserService service;

    @Test
    void findAllUsers() {
        List<NewUserRequest> sourceUsers = List.of(
                makeNewUser("ivan@email.com", "Ivan"),
                makeNewUser("petr@email.com", "Petr")
        );

        for (NewUserRequest userDto : sourceUsers) {
            User entity = new User();
            entity.setName(userDto.getName());
            entity.setEmail(userDto.getEmail());
            em.persist(entity);
        }
        em.flush();

        Collection<UserDto> targetUsers = service.findAllUsers();

        assertThat(targetUsers, hasSize(sourceUsers.size()));
        for (NewUserRequest sourceUser : sourceUsers) {
            assertThat(targetUsers, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(sourceUser.getName())),
                    hasProperty("email", equalTo(sourceUser.getEmail()))
            )));
        }
    }

    private NewUserRequest makeNewUser(String email, String name) {
        NewUserRequest dto = new NewUserRequest();
        dto.setEmail(email);
        dto.setName(name);
        return dto;
    }
}
