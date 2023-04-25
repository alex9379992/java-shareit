package ru.yandex.practicum.shareIt.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.shareIt.BaseTest;
import ru.yandex.practicum.shareIt.exeptions.PaginationException;
import ru.yandex.practicum.shareIt.exeptions.RequestNotFoundException;
import ru.yandex.practicum.shareIt.exeptions.UserNotFoundException;
import ru.yandex.practicum.shareIt.item.ItemService;
import ru.yandex.practicum.shareIt.item.model.Item;
import ru.yandex.practicum.shareIt.item.model.ItemResponseDto;
import ru.yandex.practicum.shareIt.mapper.Mapper;
import ru.yandex.practicum.shareIt.paginator.Paginator;
import ru.yandex.practicum.shareIt.request.model.IncomingRequest;
import ru.yandex.practicum.shareIt.request.model.Request;
import ru.yandex.practicum.shareIt.request.model.RequestDto;
import ru.yandex.practicum.shareIt.user.UserService;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import ru.yandex.practicum.shareIt.user.model.User;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest extends BaseTest {

    @Mock
    private RequestRepository requestRepository;
    @Mock
    private ItemService itemService;
    @Mock
    private UserService userService;
    @Mock
    private Paginator<Request> paginator;
    @Mock
    private Mapper mapper;

    @InjectMocks
    private RequestServiceImpl service;

    @Test
    void createRequestTest_WhenSaveRequest_ThenUserException() {
        IncomingRequest incomingRequest = createIncomingRequest("request");
        long userId = 1L;

        when(userService.findUserById(userId)).thenThrow(new UserNotFoundException("not found"));

        assertThrows(UserNotFoundException.class, () ->
                service.createRequest(incomingRequest, userId));
        verify(requestRepository, never()).save(any());
    }

    @Test
    void createRequestTest_WhenSaveRequest_ThenReturnRequestDto() {
        IncomingRequest incomingRequest = createIncomingRequest("request");
        User user = createStandartUser();
        Request request = createRequest(1L, user, incomingRequest.getDescription());
        RequestDto requestDto = createRequestDto(1L, user, incomingRequest.getDescription());
        long userId = 1L;

        when(userService.findUserById(userId)).thenReturn(user);
        when(mapper.toRequest(incomingRequest)).thenReturn(request);
        when(requestRepository.save(request)).thenReturn(request);
        when(mapper.toRequestDto(request)).thenReturn(requestDto);

        RequestDto afterSaveRequestDto = service.createRequest(incomingRequest, userId);
        assertEquals(requestDto.getDescription(), afterSaveRequestDto.getDescription());
        verify(requestRepository).save(request);
    }

    @Test
    void findRequestDtoListFromUserTest_WhenFindRequestDtoList_ThenReturnRequestDtoList() {
        User user = createStandartUser();
        long userId = 1L;
        Request request1 = createRequest(1L, user, "request1");
        Request request2 = createRequest(1L, user, "request2");
        RequestDto requestDto1 = createRequestDto(1L, user, "request1");
        RequestDto requestDto2 = createRequestDto(2L, user, "request2");
        Item item1 = crateItem();
        item1.setOwner(user);
        item1.setRequest(request1);
        ItemResponseDto itemResponseDto1 = createItemResponseDto(item1);
        Item item2 = crateItem();
        item2.setOwner(user);
        item2.setRequest(request2);
        ItemResponseDto itemResponseDto2 = createItemResponseDto(item2);


        when(userService.findUserById(userId)).thenReturn(user);
        when(requestRepository.findAllByRequestorId(userId)).thenReturn(List.of(request1, request2));
        when(mapper.toRequestDto(request1)).thenReturn(requestDto1);
        when(mapper.toRequestDto(request2)).thenReturn(requestDto2);
        when(itemService.findAllByRequestId(anyLong())).thenReturn(List.of(item1, item2));
        when(mapper.toItemResponseDto(item1)).thenReturn(itemResponseDto1);
        when(mapper.toItemResponseDto(item2)).thenReturn(itemResponseDto2);

        List<RequestDto> requestDtoList = service.findRequestDtoListFromUser(userId);
        assertEquals(requestDtoList.size(), 2);
    }

    @Test
    void findRequestDtoListFromUserTest_WhenFindRequestDtoList_ThenUserException() {
        long userId = 1L;

        when(userService.findUserById(userId)).thenThrow(new UserNotFoundException("not found"));

        assertThrows(UserNotFoundException.class, () ->
                      service.findRequestDtoListFromUser(userId));
    }

    @Test
    void findRequestDtoFromIdTest_WhenReturnRequestDto_ThenUserException() {
        long userId = 1L;
        Long requestId = 1L;

        when(userService.findUserById(userId)).thenThrow(new UserNotFoundException("not found"));

        assertThrows(UserNotFoundException.class, () ->
                service.findRequestDtoFromId(requestId, userId));
    }

    @Test
    void findRequestDtoFromIdTest_WhenReturnRequestDto_ThenRequestException() {
        long userId = 1L;
        Long requestId = 1L;

        when(userService.findUserById(userId)).thenReturn(any());
        when(requestRepository.findById(requestId)).thenThrow(new RequestNotFoundException("not found"));


        assertThrows(RequestNotFoundException.class, () ->
                service.findRequestDtoFromId(requestId, userId));
    }

    @Test
    void findRequestDtoFromIdTest_WhenReturnRequestDto_ThenRequest() {
        long userId = 1L;
        Long requestId = 1L;
        User user = createStandartUser();
        Request request = createRequest(1L, user, "request1");
        RequestDto requestDto = createRequestDto(1L, user, "request1");
        Item item = crateItem();
        item.setOwner(user);
        item.setRequest(request);
        ItemResponseDto itemResponseDto = createItemResponseDto(item);

        when(userService.findUserById(userId)).thenReturn(any());
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(mapper.toRequestDto(request)).thenReturn(requestDto);
        when(itemService.findAllByRequestId(anyLong())).thenReturn(List.of(item));
        when(mapper.toItemResponseDto(item)).thenReturn(itemResponseDto);

        RequestDto findRequestDto = service.findRequestDtoFromId(requestId, userId);

        assertEquals(findRequestDto.getItems().get(0).getName(), item.getName());
    }

    @Test
    void findAllRequestDtoListFromPaginationTest_WhenReturnRequestDtoList_ThenUserException() {
        long userId = 1L;
        Long from = 0L;
        Long size = 2L;

        when(userService.findUserById(userId)).thenThrow(new UserNotFoundException("not found"));

        assertThrows(UserNotFoundException.class, () ->
                service.findAllRequestDtoListFromPagination(userId, from, size));
    }

    @Test
    void findAllRequestDtoListFromPaginationTest_WhenReturnRequestDtoList_ThenReturnRequestDtoList() {
        long userId = 1L;
        Long from = 0L;
        Long size = 2L;
        User user = createStandartUser();
        Request request1 = createRequest(1L, user, "request1");
        Request request2 = createRequest(2L, user, "request2");
        RequestDto requestDto1 = createRequestDto(1L, user, "request1");
        RequestDto requestDto2 = createRequestDto(2L, user, "request2");
        Item item1 = crateItem();
        item1.setOwner(user);
        item1.setRequest(request1);
        ItemResponseDto itemResponseDto1 = createItemResponseDto(item1);
        Item item2 = crateItem();
        item2.setOwner(user);
        item2.setRequest(request2);
        ItemResponseDto itemResponseDto2 = createItemResponseDto(item2);

        when(userService.findUserById(userId)).thenReturn(user);
        when(requestRepository.findAllRequestFromOtherUser(userId)).thenReturn(List.of(request1, request2));
       when(paginator.paginationOf(List.of(request1, request2), from, size)).thenReturn(List.of(request1, request2));
        when(mapper.toRequestDtoList(List.of(request1, request2))).thenReturn(List.of(requestDto1, requestDto2));
        //when(mapper.toRequestDto(request1)).thenReturn(requestDto1);
       // when(mapper.toRequestDto(request2)).thenReturn(requestDto2);
        when(itemService.findAllByRequestId(anyLong())).thenReturn(List.of(item1, item2));
        when(mapper.toItemResponseDto(item1)).thenReturn(itemResponseDto1);
        when(mapper.toItemResponseDto(item2)).thenReturn(itemResponseDto2);

         List<RequestDto> requestDtoList = service.findAllRequestDtoListFromPagination(userId, from, size);
        assertEquals(requestDtoList.size(), 2);
    }

    @Test
    void findAllRequestDtoListFromPaginationTest_WhenReturnRequestDtoList_ThenPaginationException() {
        long userId = 1L;
        Long from = -1L;
        Long size = 2L;
        User user = createStandartUser();
        Request request1 = createRequest(1L, user, "request1");
        Request request2 = createRequest(2L, user, "request2");

        when(userService.findUserById(userId)).thenReturn(user);
        when(requestRepository.findAllRequestFromOtherUser(userId)).thenReturn(List.of(request1, request2));
        when(paginator.paginationOf(List.of(request1, request2), from, size)).thenThrow(new PaginationException("exception"));


       assertThrows(PaginationException.class, () ->
               service.findAllRequestDtoListFromPagination(userId, from, size));
    }
}