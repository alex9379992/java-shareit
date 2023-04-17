package ru.yandex.practicum.shareIt.mapper;

import org.mapstruct.Mapping;
import ru.yandex.practicum.shareIt.booking.model.Booking;
import ru.yandex.practicum.shareIt.booking.model.BookingDto;
import ru.yandex.practicum.shareIt.booking.model.BookingInItemDto;
import ru.yandex.practicum.shareIt.booking.model.BookingResponseDto;
import ru.yandex.practicum.shareIt.comment.model.Comment;
import ru.yandex.practicum.shareIt.comment.model.CommentDto;
import ru.yandex.practicum.shareIt.comment.model.CommentRequestDto;
import ru.yandex.practicum.shareIt.item.model.IncomingItem;
import ru.yandex.practicum.shareIt.item.model.Item;
import ru.yandex.practicum.shareIt.item.model.ItemDto;
import ru.yandex.practicum.shareIt.request.model.IncomingRequest;
import ru.yandex.practicum.shareIt.request.model.Request;
import ru.yandex.practicum.shareIt.request.model.RequestDto;
import ru.yandex.practicum.shareIt.response.model.Response;
import ru.yandex.practicum.shareIt.response.model.ResponseDto;
import ru.yandex.practicum.shareIt.user.model.User;
import ru.yandex.practicum.shareIt.user.model.UserDto;

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
    Item toItem(IncomingItem incomingItem);

    ItemDto toItemDto(Item item);


    default List<ItemDto> toItemDtoList(List<Item> items) {
        return items.stream().filter(Item::getAvailable).map(this::toItemDto).collect(Collectors.toList());
    }

    //requests
    Request toRequest(IncomingRequest incomingRequest);

    RequestDto toRequestDto(Request request);

    default List<RequestDto> toRequestDtoList(List<Request> requests) {
        return requests.stream().map(this::toRequestDto).collect(Collectors.toList());
    }

    //response
    @Mapping(target = "id", source = "response.item.id")
    @Mapping(target = "name", source = "response.item.name")
    @Mapping(target = "description", source = "response.item.description")
    @Mapping(target = "available", source = "response.item.available")
    @Mapping(target = "requestId", source = "response.request.id")
    @Mapping(target = "ownerId", source = "response.owner.id")
    ResponseDto toResponseDto(Response response);

    //users
    User toUser(UserDto userDto);

    UserDto toUserDto(User user);

    default List<UserDto> mapToUserDtoList(List<User> users) {
        return users.stream().map(this::toUserDto).collect(Collectors.toList());
    }
}
