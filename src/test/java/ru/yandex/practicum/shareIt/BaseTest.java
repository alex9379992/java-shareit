package ru.yandex.practicum.shareIt;


import ru.yandex.practicum.shareIt.booking.model.*;
import ru.yandex.practicum.shareIt.booking.model.dto.BookingDto;
import ru.yandex.practicum.shareIt.booking.model.dto.BookingInItemDto;
import ru.yandex.practicum.shareIt.booking.model.dto.BookingResponseDto;
import ru.yandex.practicum.shareIt.comment.model.Comment;
import ru.yandex.practicum.shareIt.comment.model.CommentDto;
import ru.yandex.practicum.shareIt.comment.model.CommentRequestDto;
import ru.yandex.practicum.shareIt.item.model.dto.IncomingItemDto;
import ru.yandex.practicum.shareIt.item.model.Item;
import ru.yandex.practicum.shareIt.item.model.dto.ItemDto;
import ru.yandex.practicum.shareIt.item.model.dto.ItemResponseDto;
import ru.yandex.practicum.shareIt.request.model.dto.IncomingRequestDto;
import ru.yandex.practicum.shareIt.request.model.Request;
import ru.yandex.practicum.shareIt.request.model.dto.RequestDto;
import ru.yandex.practicum.shareIt.user.model.User;
import ru.yandex.practicum.shareIt.user.model.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BaseTest {

    protected final String xShareUserId = "X-Sharer-User-Id";
    protected final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    protected IncomingItemDto createIncomingItem() {
        return IncomingItemDto.builder().name("отвертка").description("желтая отвертка").available(true).requestId(1L).build();
    }

    protected Item crateItem() {
        Item item = new Item();
        item.setName("отвертка");
        item.setDescription("желтая отвертка");
        item.setAvailable(true);
        return item;
    }

    protected ItemDto crateItemDto() {
        ItemDto item = new ItemDto();
        item.setName("отвертка");
        item.setDescription("желтая отвертка");
        item.setAvailable(true);
        return item;
    }

    protected static ItemDto buildItemDto(Long id, String name, String description, Boolean available) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(id);
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(available);
        itemDto.setRequestId(3L);
        return itemDto;
    }

    protected ItemResponseDto createItemResponseDto(Item item) {
        ItemResponseDto itemResponseDto = new ItemResponseDto();
        itemResponseDto.setId(item.getId());
        itemResponseDto.setName(item.getName());
        itemResponseDto.setDescription(item.getDescription());
        itemResponseDto.setAvailable(item.getAvailable());
        itemResponseDto.setRequestId(item.getRequest().getId());
        itemResponseDto.setOwnerId(item.getOwner().getId());
        return itemResponseDto;
    }

    protected UserDto createUserDto(Long id, String name, String email) {
      UserDto userDto =  new UserDto();
      userDto.setId(id);
      userDto.setName(name);
      userDto.setEmail(email);
      return userDto;
    }

    protected RequestDto createRequestDto(Long id, User user, String description) {
        RequestDto itemRequestDto = new RequestDto();
        itemRequestDto.setId(id);
        itemRequestDto.setRequestor(user);
        itemRequestDto.setDescription(description);
        itemRequestDto.setCreated(LocalDateTime.now());
        return itemRequestDto;
    }

    protected Request createRequest(Long id, User user, String description) {
        Request request = new Request();
        request.setId(id);
        request.setRequestor(user);
        request.setDescription(description);
        request.setCreated(LocalDateTime.now());
        return request;
    }

    protected User createUser (Long id, String name, String email) {
        User user =  new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        return user;
    }
    
    protected User createStandartUser() {
        User user =  new User();
        user.setId(1L);
        user.setName("alex");
        user.setEmail("alex@mai.com");
        return user;
    }

    protected IncomingRequestDto createIncomingRequest(String description) {
        IncomingRequestDto incomingRequestDto = new IncomingRequestDto();
        incomingRequestDto.setDescription(description);
        return incomingRequestDto;
    }

    protected CommentRequestDto createCommentRequestDto(String text) {
        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setText(text);
        return commentRequestDto;
    }

    protected Comment createComment(long id, String text, Item item, User user) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setText(text);
        comment.setItem(item);
        comment.setUser(user);
        comment.setCreated(LocalDateTime.now());
        return comment;
    }

    protected CommentDto createCommentDto (Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(comment.getUser().getName());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }

    protected Booking createBooking(Item item, User booker) {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusMinutes(1L));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);
        return booking;
    }

    protected Booking createBookingFromTime(Item item, User booker, LocalDateTime now) {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(now.plusMinutes(1L));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);
        return booking;
    }

    protected BookingResponseDto createBookingResponseDto() {
        return BookingResponseDto.builder()
                .itemId(1L).start(LocalDateTime.now()).end(LocalDateTime.now().plusMinutes(1L)).build();
    }

    protected BookingDto createBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setItemId(booking.getItem().getId());
        bookingDto.setItem(booking.getItem());
        bookingDto.setBooker(booking.getBooker());
        bookingDto.setStatus(booking.getStatus());
        return bookingDto;
    }
    protected static Booking buildBooking(Long id, Item item, User booker, LocalDateTime start, LocalDateTime end, BookingStatus status) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setItem(item);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setBooker(booker);
        booking.setStatus(status);
        return booking;
    }

    protected static BookingDto buildBookingDto(Long id, Item item, User booker, LocalDateTime start, LocalDateTime end, BookingStatus status) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(id);
        bookingDto.setItem(item);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        bookingDto.setBooker(booker);
        bookingDto.setStatus(status);
        return bookingDto;
    }


    protected User buildUser(Long id, String email, String name) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setName(name);
        return user;
    }
    protected Item buildItem(Long id, String name, String description, boolean available, User owner, Request itemRequest) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);
        item.setRequest(itemRequest);
        return item;
    }

    protected LocalDateTime getDateFromString(String dateString) {
        return LocalDateTime.parse(dateString, formatter);
    }

    protected BookingInItemDto buildBookingInItemDto(Long id, LocalDateTime start, LocalDateTime end, Long bookerId) {
        BookingInItemDto booking = new BookingInItemDto();
        booking.setId(id);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setBookerId(bookerId);
        return booking;
    }
}
