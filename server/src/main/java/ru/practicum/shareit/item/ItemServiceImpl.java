package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.model.CommentDto;
import ru.practicum.shareit.comment.model.CommentRequestDto;
import ru.practicum.shareit.exeptions.CommentException;
import ru.practicum.shareit.exeptions.ItemNotFoundException;
import ru.practicum.shareit.exeptions.SearchException;
import ru.practicum.shareit.exeptions.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.IncomingItemDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.mapper.Mapper;
import ru.practicum.shareit.paginator.Paginator;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;


import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Validated
@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;
    private final Paginator<ItemDto> paginator;
    private final Mapper mapper;

    @Override
    public ItemDto createItem(long userId, @Valid IncomingItemDto incomingItemDto) {
        User owner = userRepository.findById(userId).
                orElseThrow(() -> new UserNotFoundException("Пользователя с id = " + userId + " не найден"));
        Item newItem = mapper.toItem(incomingItemDto);
        newItem.setOwner(owner);
        if (incomingItemDto.getRequestId() != null) {
            newItem.setRequest(requestRepository.findById(incomingItemDto.getRequestId()).
                    orElseThrow(() -> new ItemNotFoundException("Вещь с id = " + incomingItemDto.getRequestId() + " не найдена")));
        }
        Item item = itemRepository.save(newItem);
        log.info("Новая вещь с id " + item.getId() + " сохранена");
        return mapper.toItemDto(item);

    }

    @Override
    public CommentDto createComment(long userId, long itemId, CommentRequestDto commentRequestDto) {
        if (bookingRepository.findBookingsForAddComments(itemId, userId).isEmpty()) {
            log.warn("Пользователь с id " + userId + " не бронировал или не завершил бронирование вещи с id " + itemId);
            throw new CommentException("Пользователь с id " + userId + " не бронировал или не завершил бронирование вещи с id " + itemId);
        }
        Comment comment = mapper.toComment(commentRequestDto);
        comment.setCreated(LocalDateTime.now());
        comment.setItem(itemRepository.getById(itemId));
        comment.setUser(userRepository.findById(userId).
                orElseThrow(() -> new UserNotFoundException("Пользователя с id = " + userId + " не найден")));
        log.info("Комментарий от пользователя id " + userId + " для вещи id " + itemId + " сохранен");
        return mapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public ItemDto patchItem(ItemDto itemDto, long userId, long itemId) {
        userRepository.findById(userId).
                orElseThrow(() -> new UserNotFoundException("Пользователя с id = " + userId + " не найден"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Вещь с id = " + itemDto + " не найдена"));
        itemDto.setId(itemId);
        if (item.getOwner().getId() == userId) {
            log.info("Данные о вещи с id " + itemId + " обновленны");
            return mapper.toItemDto(itemRepository.save(patcher(itemDto, item)));
        } else {
            log.warn("У вещи с id " + itemId + " не соответствует владелец с id " + userId);
            throw new SearchException("У вещи с id " + itemId + " не соответствует владелец с id " + userId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItemDto(long itemId, long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Вещь с id = " + itemId + " не найдена"));
        if (item.getOwner().getId().equals(userId)) {
            ItemDto itemDto = constructBookingForOwner(mapper.toItemDto(item));
            log.info("Вещь с id " + itemId + " отправлена");
            return itemDto;
        } else {
            return setComments(mapper.toItemDto(item));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getItemsListFromUser(long userId, Long from, Long size) {
        User owner = userRepository.findById(userId).
                orElseThrow(() -> new UserNotFoundException("Пользователя с id = " + userId + " не найден"));
        List<ItemDto> items = mapper.toItemDtoList(itemRepository.findAllByOwner(owner)).
                stream().map(this::constructBookingForOwner).
                sorted(Comparator.comparing(ItemDto::getId)).collect(Collectors.toList());
        log.info("Сформирован и отправлен список вещей пользователя с id " + userId);
        return paginator.paginationOf(items, from, size);
    }

    @Override
    public void deleteItem( long userId, long itemId) {
        userRepository.findById(userId).
                orElseThrow(() -> new UserNotFoundException("Пользователя с id = " + userId + " не найден"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Вещь с id = " + itemId + " не найдена"));
        if (item.getOwner().getId() == userId) {
            log.info("Вещь с id " + itemId + " удалена");
            itemRepository.deleteById(itemId);
        } else {
            log.warn("У вещи с id " + itemId + " не соответствует владелец с id " + userId);
            throw new SearchException("У вещи с id " + itemId + " не соответствует владелец с id " + userId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> searchItem(String text, Long from, Long size) {
        if (text.isBlank()) {
            log.warn("Отправлен пустой список вещей, так как text пустой");
            return new ArrayList<>();
        }
        log.info("Сформирован и отправлен список вещей, подходящих под описание: " + text);
        List<ItemDto> items = mapper.toItemDtoList(itemRepository.findByNameOrDescriptionContainingIgnoreCase(text, text));
        return paginator.paginationOf(items, from, size);
    }


    private Item patcher(ItemDto itemDto, Item item) {
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getOwner() != null) {
            item.setOwner(itemDto.getOwner());
        }
        return item;
    }

    private ItemDto constructBookingForOwner(ItemDto itemDto) {
        LocalDateTime now = LocalDateTime.now();
        Sort sortDesc = Sort.by("start").descending();
        Sort sortAsc = Sort.by("start").ascending();
        setComments(itemDto);
        Booking lastBooking = bookingRepository.
                findBookingByItemIdAndStartBeforeAndStatus(itemDto.getId(), now, sortDesc, BookingStatus.APPROVED)
                .stream().findFirst().orElse(null);
        Booking nextBooking = bookingRepository.
                findBookingByItemIdAndStartAfterAndStatus(itemDto.getId(), now, sortAsc, BookingStatus.APPROVED).
                stream().findFirst().orElse(null);
        if (lastBooking != null) {
            itemDto.setLastBooking(mapper.toBookingInItemDto(lastBooking));
        }
        if (nextBooking != null) {
            itemDto.setNextBooking(mapper.toBookingInItemDto(nextBooking));
        }
        return itemDto;
    }

    private ItemDto setComments(ItemDto itemDto) {
        itemDto.setComments(commentRepository.findByItemId(itemDto.getId()).
                stream().map(mapper::toCommentDto).collect(Collectors.toList()));
        return itemDto;
    }
}
