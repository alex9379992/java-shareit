package ru.yandex.practicum.shareIt.request;

import ru.yandex.practicum.shareIt.request.model.IncomingRequest;
import ru.yandex.practicum.shareIt.request.model.Request;
import ru.yandex.practicum.shareIt.request.model.RequestDto;

import java.util.List;

public interface RequestService {

    RequestDto createRequest(IncomingRequest incomingRequest, long userId);
    List<RequestDto> findRequestDtoListFromUser(long userId);
    RequestDto findRequestDtoFromId( Long requestId, long userId);
    Request findRequestById(Long requestId);
    List<RequestDto> findAllRequestDtoListFromPagination(long userId, Long from, Long size);
}
