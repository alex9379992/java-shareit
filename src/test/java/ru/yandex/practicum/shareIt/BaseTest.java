package ru.yandex.practicum.shareIt;


import ru.yandex.practicum.shareIt.item.model.IncomingItem;
import ru.yandex.practicum.shareIt.item.model.Item;
import ru.yandex.practicum.shareIt.item.model.ItemDto;
import ru.yandex.practicum.shareIt.request.model.IncomingRequest;
import ru.yandex.practicum.shareIt.request.model.RequestDto;
import ru.yandex.practicum.shareIt.response.model.ResponseDto;
import ru.yandex.practicum.shareIt.user.model.User;
import ru.yandex.practicum.shareIt.user.model.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BaseTest {

    protected final String xShareUserId = "X-Sharer-User-Id";
    protected final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    protected IncomingItem createIncomingItem() {
        return IncomingItem.builder().name("отвертка").description("желтая отвертка").available(true).requestId(1L).build();
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
        return itemRequestDto;
    }

    protected User createUser (Long id, String name, String email) {
        User user =  new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    protected IncomingRequest createIncomingRequest(String description) {
        IncomingRequest incomingRequest = new IncomingRequest();
        incomingRequest.setDescription(description);
        return incomingRequest;
    }

    protected ResponseDto createResponseDto(Long id, String name, String description, Boolean available, Long requestId,
                                            Long ownerId, LocalDateTime create) {
       ResponseDto responseDto = new ResponseDto();
       responseDto.setId(id);
       responseDto.setName(name);
       responseDto.setDescription(description);
       responseDto.setAvailable(available);
       responseDto.setRequestId(requestId);
       responseDto.setOwnerId(ownerId);
       responseDto.setCreate(create);
        return responseDto;
    }
}
