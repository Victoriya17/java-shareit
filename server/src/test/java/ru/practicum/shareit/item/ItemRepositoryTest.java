package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User owner;
    private Item item1;
    private Item item2;

    @BeforeEach
    void setUp() {
        owner = userRepository.save(new User(null, "Owner", "owner@mail.com"));

        item1 = new Item();
        item1.setName("Drill");
        item1.setDescription("Powerful electric tool");
        item1.setAvailable(true);
        item1.setOwner(owner);
        itemRepository.save(item1);

        item2 = new Item();
        item2.setName("Tool");
        item2.setDescription("Manual screwdriver");
        item2.setAvailable(true);
        item2.setOwner(owner);
        itemRepository.save(item2);

        Item item3 = new Item();
        item3.setName("Old Drill");
        item3.setDescription("Broken");
        item3.setAvailable(false);
        item3.setOwner(owner);
        itemRepository.save(item3);
    }

    @Test
    void findAllByOwnerIdTest() {
        List<Item> result = itemRepository.findAllByOwnerId(owner.getId());

        assertThat(result, hasSize(3));
        assertThat(result.get(0).getOwner().getId(), equalTo(owner.getId()));
    }

    @Test
    void findItemToBookingTestSearchByName() {
        Collection<Item> result = itemRepository.findItemToBooking("DRILL");

        assertThat(result, hasSize(1));
        assertThat(result.iterator().next().getName(), equalTo("Drill"));
    }

    @Test
    void findItemToBookingTestSearchByDescription() {
        Collection<Item> result = itemRepository.findItemToBooking("manual");

        assertThat(result, hasSize(1));
        assertThat(result.iterator().next().getDescription(), containsString("Manual"));
    }

    @Test
    void findItemToBookingTestShouldNotFindUnavailable() {
        Collection<Item> result = itemRepository.findItemToBooking("broken");

        assertThat(result, is(empty()));
    }

    @Test
    void findItemToBookingTestEmptyResult() {
        Collection<Item> result = itemRepository.findItemToBooking("unknown_tool");

        assertThat(result, is(empty()));
    }
}