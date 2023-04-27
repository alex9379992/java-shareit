package ru.yandex.practicum.shareIt.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.shareIt.BaseTest;
import ru.yandex.practicum.shareIt.item.model.Item;
import ru.yandex.practicum.shareIt.request.RequestRepository;
import ru.yandex.practicum.shareIt.request.model.Request;
import ru.yandex.practicum.shareIt.user.UserRepository;
import ru.yandex.practicum.shareIt.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = { "db.name=test"})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class ItemRepositoryTest extends BaseTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RequestRepository requestRepository;
    private User user1;

    @BeforeEach
    void setUp() {
        user1 = createUser(null, "mike", "mike@mail.com");
        user1 =userRepository.save(user1);
        User user2 = createUser(null, "anny", "anny@mail.com");
        user2 = userRepository.save(user2);

        Request request = createRequest(1L, user2, "Нужна отвертка");
        request = requestRepository.save(request);

        Item item1 = crateItem();
        item1.setId(2L);
        item1.setRequest(request);
        item1.setOwner(user1);

        itemRepository.save(item1);

        Item item2 = crateItem();
        item2.setId(3L);
        item2.setName("молоток");
        item2.setDescription("большой молоток");
        item2.setOwner(user1);
        itemRepository.save(item2);

        Item item3 = crateItem();
        item3.setName("зубило");
        item3.setDescription("зубило");
        item3.setId(4L);
        item3.setOwner(user2);
        itemRepository.save(item3);
    }

    @Test
    void testFindAllByOwner() {
        List<Item> items = itemRepository.findAllByOwner(user1);
        assertEquals(items.size(), 2);
        for(Item i : items) {
            assertEquals(i.getOwner().getId(), user1.getId());
        }
    }

    @Test
    void findAllByRequestId() {
        List<Item> items = itemRepository.findAllByRequestId(1L);
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getRequest().getDescription(), "Нужна отвертка");
    }

    @Test
    void testFindByNameOrDescriptionContainingIgnoreCase() {
        List<Item> items = itemRepository.findByNameOrDescriptionContainingIgnoreCase("МолоТок", "БоЛьшой");
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getName(), "молоток");
    }
}