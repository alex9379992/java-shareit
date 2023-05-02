package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.BaseTest;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {"db.name=test"})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class RequestRepositoryTest extends BaseTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RequestRepository requestRepository;

    @BeforeEach
    void setUp() {
        User user = createUser(null, "shon", "shon@mail.com");
        user = userRepository.save(user);
        User user2 = createUser(null, "sara", "sara@mail.com");
        user2 = userRepository.save(user2);

        Item item = crateItem();
        item.setName("бензопила");
        item.setDescription("бензопила");
        item.setId(5L);
        item.setOwner(user);
        itemRepository.save(item);

        Item item2 = crateItem();
        item2.setName("ключи");
        item2.setDescription("набор ключей");
        item2.setId(6L);
        item2.setOwner(user2);
        itemRepository.save(item2);

        Request request = createRequest(2L, user, "Нужна Бензопила");
        requestRepository.save(request);
        Request request2 = createRequest(3L, user2, "Нужны ключи");
        requestRepository.save(request2);
    }

    @Test
    void findAllByRequestorId() {
        List<Request> requestList = requestRepository.findAllByRequestorId(1L);
        assertEquals(requestList.size(), 1);
        assertEquals(requestList.get(0).getRequestor().getId(), 1L);
        assertEquals(requestList.get(0).getDescription(), "Нужна Бензопила");
    }

    @Test
    void findAllRequestFromOtherUser() {
        List<Request> requestList = requestRepository.findAllRequestFromOtherUser(5L);
        assertNotEquals(requestList.get(0).getRequestor().getId(), 5L);
        assertEquals(requestList.get(0).getDescription(), "Нужны ключи");
    }
}