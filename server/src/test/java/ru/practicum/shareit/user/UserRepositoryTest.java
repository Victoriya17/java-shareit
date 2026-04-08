package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findUserByEmailTest() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@mail.com");
        userRepository.save(user);

        Optional<User> result = userRepository.findUserByEmail("john@mail.com");

        assertThat(result.isPresent(), is(true));
        assertThat(result.get().getName(), equalTo("John"));
    }

    @Test
    void findUserByEmailWhenNotFound() {
        Optional<User> result = userRepository.findUserByEmail("unknown@mail.com");

        assertThat(result.isEmpty(), is(true));
    }
}
