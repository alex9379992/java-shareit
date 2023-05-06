package ru.practicum.shareit.request;


import ru.practicum.shareit.request.model.dto.IncomingRequestDto;
import ru.practicum.shareit.request.model.dto.RequestDto;

import java.util.List;

public interface RequestService {

    RequestDto createRequest(IncomingRequestDto incomingRequestDto, long userId);
    List<RequestDto> findRequestDtoListFromUser(long userId);
    RequestDto findRequestDtoFromId( Long requestId, long userId);
    List<RequestDto> findAllRequestDtoListFromPagination(long userId, Long from, Long size);
}
