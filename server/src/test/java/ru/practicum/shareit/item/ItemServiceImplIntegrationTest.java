package ru.practicum.shareit.item;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.OtherItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplIntegrationTest {

    private final EntityManager em;
    private final ItemService itemService;

    @Test
    void findAllIntegrationTest() {
        User owner = new User(null, "owner@mail.com", "Owner");
        em.persist(owner);

        Item item1 = new Item(null, "Item1", "Desc1", owner, true, null);
        em.persist(item1);

        em.flush();

        Collection<OtherItemDto> result = itemService.findAll(owner.getId());

        assertThat(result, hasSize(1));
        assertThat(result.iterator().next().getName(), equalTo("Item1"));
    }
}
