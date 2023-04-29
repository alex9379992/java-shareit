package ru.yandex.practicum.shareIt.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.shareIt.exeptions.RequestNotFoundException;
import ru.yandex.practicum.shareIt.exeptions.UserNotFoundException;
import ru.yandex.practicum.shareIt.item.ItemRepository;
import ru.yandex.practicum.shareIt.item.model.dto.ItemResponseDto;
import ru.yandex.practicum.shareIt.mapper.Mapper;
import ru.yandex.practicum.shareIt.paginator.Paginator;
import ru.yandex.practicum.shareIt.request.model.dto.IncomingRequestDto;
import ru.yandex.practicum.shareIt.request.model.Request;
import ru.yandex.practicum.shareIt.request.model.dto.RequestDto;
import ru.yandex.practicum.shareIt.user.UserRepository;
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
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final Paginator<Request> paginator;
    private final Mapper mapper;


    @Override
    public RequestDto createRequest(IncomingRequestDto incomingRequestDto, long userId) {
        User user = userRepository.findById(userId).
                orElseThrow(() -> new UserNotFoundException("Пользователя с id = " + userId + " не найден"));
        Request request = mapper.toRequest(incomingRequestDto);
        request.setRequestor(user);
        request.setCreated(LocalDateTime.now());
        log.info("Новый запрос сохранен");
        return mapper.toRequestDto(requestRepository.save(request));

    }

    @Override
    public List<RequestDto> findRequestDtoListFromUser(long userId) {
        userRepository.findById(userId).
                orElseThrow(() -> new UserNotFoundException("Пользователя с id = " + userId + " не найден"));
        List<RequestDto> requests = requestRepository.findAllByRequestorId(userId)
                .stream().map(mapper::toRequestDto).collect(Collectors.toList());
        for (RequestDto requestDto : requests) {
            List<ItemResponseDto> itemResponseDtoList = itemRepository.findAllByRequestId(requestDto.getId()).
                    stream().map(mapper::toItemResponseDto).collect(Collectors.toList());
            requestDto.setItems(itemResponseDtoList);
        }
        return requests.stream().sorted(Comparator.comparing(RequestDto::getCreated)).collect(Collectors.toList());

    }

    @Override
    public RequestDto findRequestDtoFromId(Long requestId, long userId) {
        userRepository.findById(userId).
                orElseThrow(() -> new UserNotFoundException("Пользователя с id = " + userId + " не найден"));
        RequestDto requestDto = mapper.toRequestDto(requestRepository.findById(requestId).
                orElseThrow(() -> new RequestNotFoundException("Вещь с id = " + requestId + " не найдена")));
        requestDto.setItems(itemRepository.findAllByRequestId(requestId).
                stream().map(mapper::toItemResponseDto).collect(Collectors.toList()));
        return requestDto;
    }

    @Override
    public List<RequestDto> findAllRequestDtoListFromPagination(long userId, Long from, Long size) {
        userRepository.findById(userId).
                orElseThrow(() -> new UserNotFoundException("Пользователя с id = " + userId + " не найден"));
        List<RequestDto> requestDtoList = mapper.toRequestDtoList(paginator.paginationOf(requestRepository.
                findAllRequestFromOtherUser(userId), from, size));
        for (RequestDto requestDto : requestDtoList) {
            requestDto.setItems(itemRepository.findAllByRequestId(requestDto.getId()).
                    stream().map(mapper::toItemResponseDto).collect(Collectors.toList()));
        }
        return requestDtoList;
    }
}
