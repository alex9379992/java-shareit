package ru.yandex.practicum.shareIt.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.shareIt.BaseTest;
import ru.yandex.practicum.shareIt.item.ItemRepository;
import ru.yandex.practicum.shareIt.item.model.Item;
import ru.yandex.practicum.shareIt.request.model.Request;
import ru.yandex.practicum.shareIt.user.UserRepository;
import ru.yandex.practicum.shareIt.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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
        User user = createUser(5L, "shon", "shon@mail.com");
        user =userRepository.save(user);
        User user2 = createUser(6L, "sara", "sara@mail.com");
        user2 =userRepository.save(user2);

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
        List<Request> requestList = requestRepository.findAllByRequestorId(5L);
        assertEquals(requestList.size(), 1);
        assertEquals(requestList.get(0).getRequestor().getId(), 5L);
        assertEquals(requestList.get(0).getDescription(), "Нужна Бензопила");
    }

    @Test
    void findAllRequestFromOtherUser() {
        List<Request> requestList = requestRepository.findAllRequestFromOtherUser(5L);
        assertNotEquals(requestList.get(0).getRequestor().getId(), 5L);
        assertEquals(requestList.get(0).getDescription(), "Нужны ключи");
    }
}