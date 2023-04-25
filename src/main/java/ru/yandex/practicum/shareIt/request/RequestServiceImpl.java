package ru.yandex.practicum.shareIt.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.shareIt.exeptions.RequestNotFoundException;
import ru.yandex.practicum.shareIt.item.ItemService;
import ru.yandex.practicum.shareIt.item.model.ItemResponseDto;
import ru.yandex.practicum.shareIt.mapper.Mapper;
import ru.yandex.practicum.shareIt.paginator.Paginator;
import ru.yandex.practicum.shareIt.request.model.IncomingRequest;
import ru.yandex.practicum.shareIt.request.model.Request;
import ru.yandex.practicum.shareIt.request.model.RequestDto;
import ru.yandex.practicum.shareIt.user.UserService;
import ru.yandex.practicum.shareIt.user.model.User;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@Service
@Slf4j
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final ItemService itemService;
    private final UserService userService;
    private final Paginator<Request> paginator;
    private final Mapper mapper;


    @Override
    public RequestDto createRequest(IncomingRequest incomingRequest, long userId) {
        User user = userService.findUserById(userId);
        Request request = mapper.toRequest(incomingRequest);
        request.setRequestor(user);
        request.setCreated(LocalDateTime.now());
        log.info("Новый запрос сохранен");
        return mapper.toRequestDto(requestRepository.save(request));

    }

    @Override
    public List<RequestDto> findRequestDtoListFromUser(long userId) {
        userService.findUserById(userId);
        List<RequestDto> requests = requestRepository.findAllByRequestorId(userId)
                .stream().map(mapper::toRequestDto).collect(Collectors.toList());
        for (RequestDto requestDto : requests) {
            List<ItemResponseDto> itemResponseDtoList = itemService.findAllByRequestId(requestDto.getId()).
                    stream().map(mapper::toItemResponseDto).collect(Collectors.toList());
            requestDto.setItems(itemResponseDtoList);
        }
        return requests.stream().sorted(Comparator.comparing(RequestDto::getCreated)).collect(Collectors.toList());

    }

    @Override
    public RequestDto findRequestDtoFromId(Long requestId, long userId) {
        userService.findUserById(userId);
        RequestDto requestDto = mapper.toRequestDto(findRequestById(requestId));
        requestDto.setItems(itemService.findAllByRequestId(requestDto.getId()).
                stream().map(mapper::toItemResponseDto).collect(Collectors.toList()));
        return requestDto;
    }

    @Override
    public Request findRequestById(Long requestId) {
        return requestRepository.findById(requestId).
                orElseThrow(() -> new RequestNotFoundException("Вещь с id = " + requestId + " не найдена"));
    }

    @Override
    public List<RequestDto> findAllRequestDtoListFromPagination(long userId, Long from, Long size) {
        userService.findUserById(userId);
        List<RequestDto> requestDtoList = mapper.toRequestDtoList(paginator.paginationOf(requestRepository.
                findAllRequestFromOtherUser(userId), from, size));
        for (RequestDto requestDto : requestDtoList) {
            requestDto.setItems(itemService.findAllByRequestId(requestDto.getId()).
                    stream().map(mapper::toItemResponseDto).collect(Collectors.toList()));
        }
        return requestDtoList;
    }
}
