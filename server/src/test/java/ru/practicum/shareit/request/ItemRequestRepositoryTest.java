package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private User requestor1;
    private User requestor2;

    @BeforeEach
    void setUp() {
        requestor1 = userRepository.save(new User(null, "User1", "user1@mail.com"));
        requestor2 = userRepository.save(new User(null, "User2", "user2@mail.com"));

        ItemRequest req1 = new ItemRequest();
        req1.setDescription("Request 1");
        req1.setRequestor(requestor1);
        req1.setCreated(LocalDateTime.now().minusDays(1));
        itemRequestRepository.save(req1);

        ItemRequest req2 = new ItemRequest();
        req2.setDescription("Request 2");
        req2.setRequestor(requestor1);
        req2.setCreated(LocalDateTime.now());
        itemRequestRepository.save(req2);

        ItemRequest req3 = new ItemRequest();
        req3.setDescription("Request 3");
        req3.setRequestor(requestor2);
        req3.setCreated(LocalDateTime.now());
        itemRequestRepository.save(req3);
    }

    @Test
    void findByRequestorIdOrderByCreatedDescTest() {
        List<ItemRequest> result = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(requestor1.getId());

        assertThat(result, hasSize(2));
        assertThat(result.get(0).getDescription(), equalTo("Request 2"));
        assertThat(result.get(1).getDescription(), equalTo("Request 1"));
    }

    @Test
    void findAllByRequestorIdNotOrderByCreatedDescTest() {
        List<ItemRequest> result = itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(requestor1.getId());

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getRequestor().getId(), equalTo(requestor2.getId()));
        assertThat(result.get(0).getDescription(), equalTo("Request 3"));
    }
}
