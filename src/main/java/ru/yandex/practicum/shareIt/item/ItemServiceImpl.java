package ru.yandex.practicum.shareIt.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.shareIt.booking.model.Booking;
import ru.yandex.practicum.shareIt.booking.BookingRepository;
import ru.yandex.practicum.shareIt.booking.model.BookingMapper;
import ru.yandex.practicum.shareIt.booking.model.BookingStatus;
import ru.yandex.practicum.shareIt.item.comment.model.CommentMapper;
import ru.yandex.practicum.shareIt.item.comment.CommentRepository;
import ru.yandex.practicum.shareIt.item.comment.model.Comment;
import ru.yandex.practicum.shareIt.item.comment.model.CommentDto;
import ru.yandex.practicum.shareIt.item.comment.model.CommentRequestDto;
import ru.yandex.practicum.shareIt.exeptions.CommentException;
import ru.yandex.practicum.shareIt.exeptions.SearchException;
import ru.yandex.practicum.shareIt.item.model.Item;
import ru.yandex.practicum.shareIt.item.model.ItemDto;
import ru.yandex.practicum.shareIt.item.model.ItemMapper;
import ru.yandex.practicum.shareIt.user.UserRepository;
import ru.yandex.practicum.shareIt.user.model.UserMapper;
import ru.yandex.practicum.shareIt.user.model.UserValidator;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;
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
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    @Override
    public ItemDto createItem(long userId, @Valid ItemDto itemDto) {
        if (UserValidator.isThereAUser(userId, userMapper.mapToUsersMap(userRepository.findAll()))) {
            itemDto.setOwner(userRepository.getById(userId));
            Item item = itemRepository.save(itemMapper.toItem(itemDto));
            log.info("Новая вещь с id " + item.getId() + " сохранена");
            return itemMapper.toItemDto(item);
        } else {
            log.warn("Пользователь с id " + userId + " не найден");
            throw new SearchException("Пользователь с id " + userId + " не найден");
        }
    }

    @Override
    public CommentDto createComment(long userId, long itemId, CommentRequestDto commentRequestDto) {
        if (getItemsMapFromList().containsKey(itemId)) {
            if (bookingRepository.findBookingsForAddComments(itemId, userId).isEmpty()) {
                log.warn("Пользователь с id " + userId + " не бронировал или не завершил бронирование вещи с id " + itemId);
                throw new CommentException("Пользователь с id " + userId + " не бронировал или не завершил бронирование вещи с id " + itemId);
            }
            Comment comment = commentMapper.toComment(commentRequestDto);
            comment.setCreated(LocalDateTime.now());
            comment.setItem(itemRepository.getById(itemId));
            comment.setUser(userRepository.getById(userId));
            log.info("Комментарий от пользователя id " + userId + " для вещи id " + itemId + " сохранен");
            return commentMapper.toCommentDto(commentRepository.save(comment));
        } else {
            log.warn("Вещь с id " + itemId + " не найдена");
            throw new SearchException("Вещь с id " + itemId + " не найдена");
        }
    }

    @Override
    public ItemDto patchItem(ItemDto itemDto, int userId, long itemId) {
        if (UserValidator.isThereAUser(userId, userMapper.mapToUsersMap(userRepository.findAll()))) {
            if (getItemsMapFromList().containsKey(itemId)) {
                itemDto.setId(itemId);
                Item item = getItemById(itemId);
                if (getItemById(itemId).getOwner().getId() == userId) {
                    log.info("Данные о вещи с id " + itemId + " обновленны");
                    return itemMapper.toItemDto(itemRepository.save(patcher(itemDto, item)));
                } else {
                    log.warn("У вещи с id " + itemId + " не соответствует владелец с id " + userId);
                    throw new SearchException("У вещи с id " + itemId + " не соответствует владелец с id " + userId);
                }
            } else {
                log.warn("Вещь с id " + itemId + " не найдена");
                throw new SearchException("Вещь с id " + itemId + " не найдена");
            }
        } else {
            log.warn("Пользователь с id " + userId + " не найден");
            throw new SearchException("Пользователь с id " + userId + " не найден");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItemDto(long itemId, long userId) {
        if (getItemsMapFromList().containsKey(itemId)) {
            if (getItemById(itemId).getOwner().getId().equals(userId)) {
                ItemDto itemDto = constructBookingForOwner(itemMapper.toItemDto(getItemById(itemId)));
                log.info("Вещь с id " + itemId + " отправлена");
                return itemDto;
            } else {
                return setComments(itemMapper.toItemDto(getItemById(itemId)));
            }
        } else {
            log.warn("Вещь с id " + itemId + " не найдена");
            throw new SearchException("Вещь с id " + itemId + " не найдена");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getItemsListFromUser(long userId) {
        if (UserValidator.isThereAUser(userId, userMapper.mapToUsersMap(userRepository.findAll()))) {
            log.info("Сформирован и отправлен список вещей пользователя с id " + userId);
            return itemMapper.toItemDtoList(itemRepository.findAllByOwner(userRepository.getById(userId))).
                    stream().map(this::constructBookingForOwner).
                    sorted(Comparator.comparing(ItemDto::getId)).collect(Collectors.toList());
        } else {
            log.warn("Пользователь с id " + userId + " не найден");
            throw new SearchException("Пользователь с id " + userId + " не найден");
        }
    }

    @Override
    public void deleteItem(int userId, long itemId) {
        if (UserValidator.isThereAUser(userId, userMapper.mapToUsersMap(userRepository.findAll()))) {
            if (getItemsMapFromList().containsKey(itemId)) {
                Item item = getItemById(itemId);
                if (item.getOwner().getId() == userId) {
                    log.info("Вещь с id " + itemId + " удалена");
                    itemRepository.deleteById(itemId);
                } else {
                    log.warn("У вещи с id " + itemId + " не соответствует владелец с id " + userId);
                    throw new SearchException("У вещи с id " + itemId + " не соответствует владелец с id " + userId);
                }
            } else {
                log.warn("Вещь с id " + itemId + " не найдена");
                throw new SearchException("Вещь с id " + itemId + " не найдена");
            }
        } else {
            log.warn("Пользователь с id " + userId + " не найден");
            throw new SearchException("Пользователь с id " + userId + " не найден");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> searchItem(String text) {
        if (text.isBlank()) {
            log.warn("Отправлен пустой список вещей, так как text пустой");
            return new ArrayList<>();
        }
        log.info("Сформирован и отправлен список вещей, подходящих под описание: " + text);
        return itemMapper.toItemDtoList(itemRepository.findByNameOrDescriptionContainingIgnoreCase(text, text));
    }

    private Item getItemById(long id) {
        Optional<Item> itemOptional = itemRepository.findById(id);
        if (itemOptional.isPresent()) {
            return itemOptional.get();
        } else {
            log.warn("Обьекта не существует");
            throw new NullPointerException("Обьекта не существует");
        }
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
            itemDto.setLastBooking(bookingMapper.toBookingInItemDto(lastBooking));
        }
        if (nextBooking != null) {
            itemDto.setNextBooking(bookingMapper.toBookingInItemDto(nextBooking));
        }
        return itemDto;
    }

    private ItemDto setComments(ItemDto itemDto) {
        itemDto.setComments(commentRepository.findByItemId(itemDto.getId()).
                stream().map(commentMapper:: toCommentDto).collect(Collectors.toList()));
        return itemDto;
    }

    private Map<Long, Item> getItemsMapFromList() {
        return itemRepository.findAll().stream().collect(Collectors.toMap(Item::getId, item -> item));
    }
}
