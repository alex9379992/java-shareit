package ru.practicum.shareit.mapper;

import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingInItemDto;
import ru.practicum.shareit.booking.model.dto.BookingResponseDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.model.CommentDto;
import ru.practicum.shareit.comment.model.CommentRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.IncomingItemDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemResponseDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.model.dto.IncomingRequestDto;
import ru.practicum.shareit.request.model.dto.RequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@org.mapstruct.Mapper(componentModel = "spring")
public interface Mapper {

    //bookings
    Booking toBooking(BookingResponseDto bookingResponseDto);

    BookingDto toBookingDto(Booking booking);

    @Mapping(target = "bookerId", source = "booking.booker.id")
    BookingInItemDto toBookingInItemDto(Booking booking);

    default List<BookingDto> mapToBookingDtoList(List<Booking> bookings) {
        return bookings.stream().map(this::toBookingDto).collect(Collectors.toList());
    }

    //comments
    @Mapping(target = "authorName", source = "comment.user.name")
    CommentDto toCommentDto(Comment comment);

    Comment toComment(CommentRequestDto commentRequestDto);

    //items
    Item toItem(IncomingItemDto incomingItemDto);

    @Mapping(target = "requestId" , source = "item.request.id")
    ItemDto toItemDto(Item item);

    @Mapping(target = "requestId", source = "request.id")
    @Mapping(target = "ownerId", source = "owner.id")
    ItemResponseDto toItemResponseDto(Item item);


    default List<ItemDto> toItemDtoList(List<Item> items) {
        return items.stream().filter(Item::getAvailable).map(this::toItemDto).collect(Collectors.toList());
    }

    //requests
    Request toRequest(IncomingRequestDto incomingRequestDto);

    RequestDto toRequestDto(Request request);

    default List<RequestDto> toRequestDtoList(List<Request> requests) {
        return requests.stream().map(this::toRequestDto).collect(Collectors.toList());
    }



    //users
    User toUser(UserDto userDto);

    UserDto toUserDto(User user);

    default List<UserDto> mapToUserDtoList(List<User> users) {
        return users.stream().map(this::toUserDto).collect(Collectors.toList());
    }

    default User patcher(UserDto userDto, User user) {
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        return user;
    }
}
