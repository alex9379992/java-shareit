package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.BaseTest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.model.CommentDto;
import ru.practicum.shareit.comment.model.CommentRequestDto;
import ru.practicum.shareit.exeptions.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.IncomingItemDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.mapper.Mapper;
import ru.practicum.shareit.paginator.Paginator;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


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
    private RequestRepository requestRepository;
    @Mock
    private Paginator<ItemDto> paginator;
    @Mock
    private Mapper mapper;

    @InjectMocks
    private ItemServiceImpl itemService;


    @Test
    void createItemTest_whenSaveItem_ThenSaveItem() {
        long userId = 1L;
        User owner = createStandartUser();
        IncomingItemDto incomingItemDto = createIncomingItem();
        Item item = crateItem();
        ItemDto itemDto = crateItemDto();

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(mapper.toItem(incomingItemDto)).thenReturn(item);
        when(requestRepository.findById(userId)).thenReturn(Optional.of(createRequest(1L, owner, "отвертка")));
        when(itemRepository.save(item)).thenReturn(item);
        when(mapper.toItemDto(any(Item.class))).thenReturn(itemDto);

        ItemDto afterSaveItemDto = itemService.createItem(userId, incomingItemDto);
        verify(itemRepository).save(any());
        assertEquals(itemDto.getName(), afterSaveItemDto.getName());
    }

    @Test
    void createItemTest_whenSaveItem_ThenUserNotFound() {
        long userId = 1L;
        IncomingItemDto incomingItemDto = createIncomingItem();

        when(userRepository.findById(userId)).thenThrow(new UserNotFoundException("Пользователь не найден"));
        assertThrows(UserNotFoundException.class, () ->
                itemService.createItem(userId, incomingItemDto));
    }

    @Test
    void createItemTest_whenSaveItem_ThenItemNotFound() {
        long userId = 1L;
        User owner = createStandartUser();
        IncomingItemDto incomingItemDto = createIncomingItem();
        Item item = crateItem();

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(mapper.toItem(incomingItemDto)).thenReturn(item);

        when(requestRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () ->
                itemService.createItem(userId, incomingItemDto));
    }

    @Test
    void createCommentTest_WhenSaveComment_ThenSaveComment() {
        long userId = 1L;
        long itemId = 1L;
        User user = createStandartUser();
        Item item = crateItem();
        CommentRequestDto commentRequestDto = createCommentRequestDto("good");
        Comment comment = createComment(1L, commentRequestDto.getText(), item, user);
        CommentDto commentDto = createCommentDto(comment);


        when(bookingRepository.findBookingsForAddComments(itemId, userId)).thenReturn(List.of(new Booking()));
        when(mapper.toComment(commentRequestDto)).thenReturn(comment);
        when(itemRepository.getById(itemId)).thenReturn(item);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(commentRepository.save(comment)).thenReturn(comment);
        when(mapper.toCommentDto(comment)).thenReturn(commentDto);

        CommentDto afterSaveCommentDto = itemService.createComment(userId, itemId, commentRequestDto);
        assertEquals(commentDto.getText(), afterSaveCommentDto.getText());
    }

    @Test
    void createCommentTest_WhenSaveComment_ThenTrowCommentException() {
        long userId = 1L;
        long itemId = 1L;
        CommentRequestDto commentRequestDto = createCommentRequestDto("good");

        when(bookingRepository.findBookingsForAddComments(anyLong(), anyLong())).thenReturn(List.of());


        assertThrows(CommentException.class, () ->
                itemService.createComment(userId, itemId, commentRequestDto));
        verify(commentRepository, never()).save(any());
    }

    @Test
    void createCommentTest_WhenSaveComment_ThenTrowUserException() {
        long userId = 1L;
        long itemId = 1L;
        User user = createStandartUser();
        Item item = crateItem();
        CommentRequestDto commentRequestDto = createCommentRequestDto("good");
        Comment comment = createComment(1L, commentRequestDto.getText(), item, user);

        when(bookingRepository.findBookingsForAddComments(itemId, userId)).thenReturn(List.of(new Booking()));
        when(mapper.toComment(commentRequestDto)).thenReturn(comment);
        when(itemRepository.getById(itemId)).thenReturn(item);
        when(userRepository.findById(userId)).thenThrow(new UserNotFoundException("Пользователь не найден"));

        assertThrows(UserNotFoundException.class, () ->
                itemService.createComment(userId, itemId, commentRequestDto));
        verify(commentRepository, never()).save(any());
    }

    @Test
    void createCommentTest_WhenSaveComment_ThenTrowItemException() {
        long userId = 1L;
        long itemId = 1L;
        User user = createStandartUser();
        Item item = crateItem();
        CommentRequestDto commentRequestDto = createCommentRequestDto("good");
        Comment comment = createComment(1L, commentRequestDto.getText(), item, user);

        when(bookingRepository.findBookingsForAddComments(itemId, userId)).thenReturn(List.of(new Booking()));
        when(mapper.toComment(commentRequestDto)).thenReturn(comment);
        when(itemRepository.getById(itemId)).thenThrow(new ItemNotFoundException("итем не найден"));


        assertThrows(ItemNotFoundException.class, () ->
                itemService.createComment(userId, itemId, commentRequestDto));
        verify(commentRepository, never()).save(any());
    }

    @Test
    void patchItemTest_WhenPatchItem_ThenThrowUserException() {
        ItemDto itemDto = crateItemDto();
        long userId = 1L;
        long itemId = 1L;

        when(userRepository.findById(userId)).thenThrow(new UserNotFoundException("Пользователь не найден"));

        assertThrows(UserNotFoundException.class, () ->
                itemService.patchItem(itemDto, userId, itemId));
        verify(itemRepository, never()).save(any());
    }

    @Test
    void patchItemTest_WhenPatchItem_ThenThrowItemException() {
        ItemDto itemDto = crateItemDto();
        long userId = 1L;
        long itemId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(itemRepository.findById(itemId)).thenThrow(new ItemNotFoundException("Вещь не найдена"));

        assertThrows(ItemNotFoundException.class, () ->
                itemService.patchItem(itemDto, userId, itemId));
        verify(itemRepository, never()).save(any());
    }

    @Test
    void patchItemTest_WhenPatchItem_ThenThrowSearchException() {
        ItemDto itemDto = crateItemDto();
        Item item = crateItem();
        User owner = createUser(2L, "alex", "alex@mail.com");
        item.setOwner(owner);
        item.setId(2L);
        long userId = 1L;
        long itemId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(SearchException.class, () ->
                itemService.patchItem(itemDto, userId, itemId));
        verify(itemRepository, never()).save(any());
    }

    @Test
    void patchItemTest_WhenPatchItem_ThenPatchItem() {
        ItemDto itemDto = crateItemDto();
        Item item = crateItem();
        User owner = createUser(1L, "alex", "alex@mail.com");
        item.setOwner(owner);
        item.setId(1L);
        long userId = 1L;
        long itemId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);
        when(mapper.toItemDto(item)).thenReturn(itemDto);

        ItemDto afterPatchItemDto = itemService.patchItem(itemDto, userId, itemId);
        verify(itemRepository).save(any());
        assertEquals(itemDto.getName(), afterPatchItemDto.getName());
    }

    @Test
    void getItemDtoTest_WhenGetItem_ThenItemException() {
        long userId = 1L;
        long itemId = 1L;

        when(itemRepository.findById(itemId)).thenThrow(new ItemNotFoundException("Вещь не найдена"));

        assertThrows(ItemNotFoundException.class, () ->
                itemService.getItemDto(itemId, userId));
    }

    @Test
    void getItemDtoTest_WhenGetItem_ThenGetItem() {
        long userId = 1L;
        long itemId = 1L;
        Item item = crateItem();
        User user = createStandartUser();
        item.setOwner(user);
        item.setId(itemId);
        ItemDto itemDto = crateItemDto();
        itemDto.setId(1L);
        Comment comment = createComment(1L, "abc", item, user);
        CommentDto commentDto = createCommentDto(comment);


        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(mapper.toItemDto(item)).thenReturn(itemDto);
        when(commentRepository.findByItemId(itemId)).thenReturn(List.of(comment));
        when(mapper.toCommentDto(comment)).thenReturn(commentDto);

        ItemDto findItemDto = itemService.getItemDto(itemId, userId);

        assertEquals(itemDto.getName(), findItemDto.getName());
    }

    @Test
    void getItemsListFromUserTest_WhenGetList_ThenUserException() {
        long userId = 1L;
        Long from = 0L;
        Long size = 2L;

        when(userRepository.findById(userId)).thenThrow(new UserNotFoundException("пользователь не найден"));

        assertThrows(UserNotFoundException.class,
                () -> itemService.getItemsListFromUser(userId, from, size));
    }

    @Test
    void getItemsListFromUserTest_WhenGetList_ThenPaginationException() {
        long userId = 1L;
        Long from = -1L;
        Long size = 2L;
        User owner = createStandartUser();
        Item item1 = crateItem();
        Item item2 = crateItem();
        item1.setId(1L);
        item2.setId(2L);
        ItemDto itemDto1 = crateItemDto();
        ItemDto itemDto2 = crateItemDto();
        itemDto1.setId(1L);
        itemDto2.setId(2L);
        Booking lastBooking = createBooking(item1, owner);
        Booking nextBooking = createBooking(item2, owner);

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwner(owner)).thenReturn(List.of(item1, item2));
        when(mapper.toItemDtoList(anyList())).thenReturn(List.of(itemDto1, itemDto2));
        when(bookingRepository.findBookingByItemIdAndStartBeforeAndStatus(any(), any(), any(), any())).
                thenReturn(List.of(lastBooking, nextBooking));
        when(bookingRepository.findBookingByItemIdAndStartAfterAndStatus(any(), any(), any(), any())).
                thenReturn(List.of(lastBooking, nextBooking));
        when(mapper.toItemDtoList(List.of(item1, item2))).thenReturn(List.of(itemDto1, itemDto2));
        when(paginator.paginationOf(List.of(itemDto1, itemDto2), from, size)).thenThrow(new PaginationException("paginationException"));


        assertThrows(PaginationException.class,
                () -> itemService.getItemsListFromUser(userId, from, size));
    }

    @Test
    void getItemsListFromUserTest_WhenGetList_ThenGetItemsList() {
        long userId = 1L;
        Long from = 0L;
        Long size = 2L;
        User owner = createStandartUser();
        Item item1 = crateItem();
        Item item2 = crateItem();
        item1.setId(1L);
        item2.setId(2L);
        ItemDto itemDto1 = crateItemDto();
        ItemDto itemDto2 = crateItemDto();
        itemDto1.setId(1L);
        itemDto2.setId(2L);
        Booking lastBooking = createBooking(item1, owner);
        Booking nextBooking = createBooking(item2, owner);

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwner(owner)).thenReturn(List.of(item1, item2));
        when(mapper.toItemDtoList(anyList())).thenReturn(List.of(itemDto1, itemDto2));
        when(bookingRepository.findBookingByItemIdAndStartBeforeAndStatus(any(), any(), any(), any())).
                thenReturn(List.of(lastBooking, nextBooking));
        when(bookingRepository.findBookingByItemIdAndStartAfterAndStatus(any(), any(), any(), any())).
                thenReturn(List.of(lastBooking, nextBooking));
        when(mapper.toItemDtoList(List.of(item1, item2))).thenReturn(List.of(itemDto1, itemDto2));
        when(paginator.paginationOf(List.of(itemDto1, itemDto2), from, size)).thenReturn(List.of(itemDto1, itemDto2));


        List<ItemDto> itemDtoList = itemService.getItemsListFromUser(userId, from, size);
        assertEquals(itemDtoList.size(), 2);
    }

    @Test
    void findAllByOwnerTest_WhenFindItemsList_ThenFindItemsList() {
        Item item = crateItem();
        when(itemRepository.findAllByOwner(any())).thenReturn(List.of(item));

        List<Item> items = itemRepository.findAllByOwner(any());

        assertEquals(items.size(), 1);
    }

    @Test
    void deleteItemTest_WhenDeleteItem_ThenUserException() {
        long userId = 1;
        long itemId = 1L;

        when(userRepository.findById(userId)).thenThrow(new UserNotFoundException("пользователь не найден"));

        assertThrows(UserNotFoundException.class,
                () -> itemService.deleteItem(userId, itemId));
    }

    @Test
    void deleteItemTest_WhenDeleteItem_ThenItemException() {
        long userId = 1;
        long itemId = 1L;
        User user = createStandartUser();
        Item item = crateItem();
        item.setOwner(user);


        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenThrow(new ItemNotFoundException("вещь не найдена"));


        assertThrows(ItemNotFoundException.class,
                () -> itemService.deleteItem(userId, itemId));
    }

    @Test
    void deleteItemTest_WhenDeleteItem_ThenSearchException() {
        long userId = 2;
        long itemId = 1L;
        User user = createStandartUser();
        Item item = crateItem();
        item.setOwner(user);


        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));


        assertThrows(SearchException.class,
                () -> itemService.deleteItem(userId, itemId));
    }

    @Test
    void deleteItemTest_WhenDeleteItem_ThenDeleteItem() {
        long userId = 1;
        long itemId = 1L;
        User user = createStandartUser();
        Item item = crateItem();
        item.setOwner(user);


        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        itemService.deleteItem(userId, itemId);

        verify(itemRepository).deleteById(itemId);
    }

    @Test
    void searchItemTest_WhenGetItemsList_ThenReturnEmptyList() {
        String text = "";
        Long from = 0L;
        Long size = 2L;

        List<ItemDto> itemDtoList = itemService.searchItem(text, from, size);
        assertEquals(itemDtoList.size(), 0);
    }

    @Test
    void searchItemTest_WhenGetItemsList_ThenPaginationException() {
        String text = "отвертка";
        Long from = -1L;
        Long size = 2L;
        Item item = crateItem();
        ItemDto itemDto = crateItemDto();


        when(itemRepository.findByNameOrDescriptionContainingIgnoreCase(anyString(), anyString())).thenReturn(List.of(item));
        when(mapper.toItemDtoList(anyList())).thenReturn(List.of(itemDto));
        when(paginator.paginationOf(List.of(itemDto), from, size)).thenThrow(new PaginationException("paginationException"));

        assertThrows(PaginationException.class,
                () -> itemService.searchItem(text, from, size));
    }

    @Test
    void searchItemTest_WhenGetItemsList_ThenItemsList() {
        String text = "отвертка";
        Long from = 0L;
        Long size = 2L;
        Item item = crateItem();
        ItemDto itemDto = crateItemDto();


        when(itemRepository.findByNameOrDescriptionContainingIgnoreCase(anyString(), anyString())).thenReturn(List.of(item));
        when(mapper.toItemDtoList(anyList())).thenReturn(List.of(itemDto));
        when(paginator.paginationOf(List.of(itemDto), from, size)).thenReturn(List.of(itemDto));

        List<ItemDto> itemDtoList = itemService.searchItem(text, from, size);
        assertEquals(itemDtoList.size(), 1);
    }

    @Test
    void findItemByIdTest_WhenFindItem_ThenItemException() {
        when(itemRepository.findById(anyLong())).thenThrow(new ItemNotFoundException("not found"));

        assertThrows(ItemNotFoundException.class, () ->
                itemRepository.findById(anyLong()));
    }
}