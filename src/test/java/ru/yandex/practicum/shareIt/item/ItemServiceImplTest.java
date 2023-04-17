package ru.yandex.practicum.shareIt.item;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.shareIt.BaseTest;
import ru.yandex.practicum.shareIt.booking.BookingRepository;
import ru.yandex.practicum.shareIt.comment.CommentRepository;
import ru.yandex.practicum.shareIt.item.model.ItemDto;
import ru.yandex.practicum.shareIt.paginator.Paginator;
import ru.yandex.practicum.shareIt.request.RequestRepository;
import ru.yandex.practicum.shareIt.response.ResponseRepository;
import ru.yandex.practicum.shareIt.user.UserRepository;


@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest extends BaseTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ResponseRepository responseRepository;
    @Mock
    private RequestRepository requestRepository;
    @Spy
    private Paginator<ItemDto> paginator;

    @InjectMocks
    private ItemServiceImpl itemService;

//    @Test
//    void createItemTest_whenSaveItem_ThenSaveItem() {
//        long userId = 1L;
//        User user = createUser();
//        IncomingItem incomingItem = createIncomingItem();
//        Item item = crateItem();
//        Map<Long, User> userMap = Map.of(1L, user);
//
//        when(userRepository.findAll()).thenReturn(List.of(user));
//  //
//        when(userMapper.mapToUsersMap(List.of(user))).thenReturn(Map.of(1L, user));
//       when(userValidator.isThereAUser(1L, userMap)).thenReturn(true);
//        when(itemMapper.toItem(incomingItem)).thenReturn(crateItem());
//        when(userRepository.getById(user.getId())).thenReturn(user);
//        when(itemRepository.save(any())).thenReturn(item);
//        when(itemMapper.toItemDto(item)).thenReturn(crateItemDto());
//        item.setId(1L);
////
// //       when(requestRepository.getById(anyLong())).thenReturn(creq);
//       //when(itemRepository.save(any())).thenReturn(crateItem());
//
//        ItemDto itemDto = itemService.createItem(userId, incomingItem);
//        verify(itemRepository).save(item);
//        //Item afterSaveItem = itemArgumentCaptor.getValue();
//
//        item.setId(1L);
//        //assertEquals(item.getId(), afterSaveItem.getId());
//    }
//    private User createUser() {
//        User user = new User();
//        user.setId(1L);
//        user.setName("alex");
//        user.setEmail("alex@mail.com");
//        return user;
//    }
}