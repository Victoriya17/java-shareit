package ru.practicum.shareit.request;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplIntegrationTest {

    private final EntityManager em;
    private final ItemRequestService itemRequestService;

    @Test
    void findAllItemRequestsIntegrationTest() {
        User requestor = new User(null, "req@mail.com", "Requestor");
        em.persist(requestor);

        User otherUser = new User(null, "other@mail.com", "Other");
        em.persist(otherUser);

        ItemRequest request = new ItemRequest(null, "Looking for a saw", otherUser, LocalDateTime.now());
        em.persist(request);
        em.flush();

        Collection<ItemRequestDto> result = itemRequestService.findAllItemRequests(requestor.getId());

        assertThat(result, hasSize(1));
        assertThat(result.iterator().next().getDescription(), equalTo("Looking for a saw"));
    }
}