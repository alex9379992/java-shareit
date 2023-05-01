package ru.yandex.practicum.shareIt.request;

import ru.yandex.practicum.shareIt.request.model.dto.IncomingRequestDto;
import ru.yandex.practicum.shareIt.request.model.dto.RequestDto;

import java.util.List;

public interface RequestService {

    RequestDto createRequest(IncomingRequestDto incomingRequestDto, long userId);
    List<RequestDto> findRequestDtoListFromUser(long userId);
    RequestDto findRequestDtoFromId( Long requestId, long userId);
    List<RequestDto> findAllRequestDtoListFromPagination(long userId, Long from, Long size);
}
