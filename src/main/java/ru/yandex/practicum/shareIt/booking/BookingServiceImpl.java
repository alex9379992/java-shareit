package ru.yandex.practicum.shareIt.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.shareIt.booking.model.*;
import ru.yandex.practicum.shareIt.exeptions.SearchException;
import ru.yandex.practicum.shareIt.exeptions.StateException;
import ru.yandex.practicum.shareIt.item.model.Item;
import ru.yandex.practicum.shareIt.item.model.ItemMapper;
import ru.yandex.practicum.shareIt.item.ItemRepository;
import ru.yandex.practicum.shareIt.user.model.UserMapper;
import ru.yandex.practicum.shareIt.user.UserRepository;
import ru.yandex.practicum.shareIt.user.model.UserValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Validated
@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final Sort sort = Sort.by("start").descending();
    private final BookingMapper bookingMapper;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;


    @Override
    public BookingDto createBooking(@Validated BookingResponseDto responseDto, long userId) {
        if (UserValidator.isThereAUser(userId, userMapper.mapToUsersMap(userRepository.findAll()))) {
            if (itemRepository.findAll().stream().collect(Collectors.toMap(Item::getId, item -> item)).containsKey(responseDto.getItemId())) {
                if (itemRepository.getById(responseDto.getItemId()).getAvailable()) {
                    if (!responseDto.getEnd().isAfter(responseDto.getStart())) {
                        log.warn("Ошибка валидации даты/времени");
                        throw new IllegalArgumentException("Ошибка валидации даты/времени");
                    }
                    Booking booking = bookingMapper.toBooking(responseDto);
                    booking.setItem(itemRepository.getById(responseDto.getItemId()));
                    booking.setBooker(userRepository.getById(userId));
                    booking.setStatus(BookingStatus.WAITING);
                    if (userId == booking.getItem().getId()) {
                        throw new SearchException("Пользователь не может сам у себя бронировать вещи");
                    }
                    log.info("Бронирование зарегестрированно");
                    return bookingMapper.toBookingDto(bookingRepository.save(booking));
                } else {
                    log.warn("Вещь с id " + responseDto.getItemId() + " недоступна для заказа");
                    throw new IllegalArgumentException("Вещь с id " + responseDto.getItemId() + " недоступна для заказа");
                }
            } else {
                log.warn("Вещь с id " + responseDto.getItemId() + " не найдена");
                throw new SearchException("Вещь с id " + responseDto.getItemId() + " не найдена");
            }
        } else {
            log.warn("Пользователь с id " + userId + " не найден");
            throw new SearchException("Пользователь с id " + userId + " не найден");
        }
    }

    public BookingDto patchBooking(long bookingId, boolean approved, long userId) {
        if (getBookingsMap().containsKey(bookingId)) {
            Booking booking = bookingRepository.getById(bookingId);
            if (itemRepository.getById(booking.getItem().getId()).getOwner().getId().equals(userId)) {
                if (booking.getStatus().name().equals(BookingStatus.APPROVED.name()) ||
                        booking.getStatus().name().equals(BookingStatus.REJECTED.name())) {
                    log.warn("Данное бронирование уже рассмотрено");
                    throw new IllegalArgumentException("Данное бронирование уже рассмотрено");
                }
                if (approved) {
                    booking.setStatus(BookingStatus.APPROVED);
                } else {
                    booking.setStatus(BookingStatus.REJECTED);
                }
                bookingRepository.save(booking);
                log.info("Бронирование обновлено");
                return bookingMapper.toBookingDto(booking);
            } else {
                log.warn("Пользователь с id " + userId + " не найден");
                throw new SearchException("Пользователь с id " + userId + " не найден");
            }
        } else {
            log.warn("Бронирования с id " + bookingId + " не найдено");
            throw new SearchException("Бронирования с id " + bookingId + " не найдено");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto findBookingDtoById(long bookingId, long userId) {
        if (getBookingsMap().containsKey(bookingId)) {
            Booking booking = bookingRepository.getById(bookingId);
            if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
                log.info("Бронирование id = " + bookingId + " отправленно пользователю id = " + userId);
                return bookingMapper.toBookingDto(booking);
            } else {
                log.warn("Пользователь с id " + userId + " не имеет доступа к данному бронированию");
                throw new SearchException("Пользователь с id " + userId + " не имеет доступа к данному бронированию");
            }
        } else {
            log.warn("Бронирования с id " + bookingId + " не найдено");
            throw new SearchException("Бронирования с id " + bookingId + " не найдено");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> findAllBookingsByBooker(String state, long userId) {
        if (UserValidator.isThereAUser(userId, userMapper.mapToUsersMap(userRepository.findAll()))) {
            if (state.equals(StateStatus.ALL.name())) {
                return bookingMapper.mapToBookingDtoList(bookingRepository.findByBookerId(userId, sort));
            }
            if (state.equals(StateStatus.CURRENT.name())) {
                return bookingMapper.mapToBookingDtoList(bookingRepository.findByBookerIdCurrent(userId));
            }
            if (state.equals(StateStatus.PAST.name())) {
                return bookingMapper.mapToBookingDtoList(bookingRepository.
                        findByBookerIdAndEndIsBefore(userId, LocalDateTime.now(), sort));
            }
            if (state.equals(StateStatus.FUTURE.name())) {
                return bookingMapper.mapToBookingDtoList(bookingRepository.
                        findByBookerIdAndStartIsAfter(userId, LocalDateTime.now(), sort));
            }
            if (state.equals(StateStatus.WAITING.name())) {
                return bookingMapper.mapToBookingDtoList(bookingRepository.
                        findByBookerIdAndStatus(userId, BookingStatus.WAITING, sort));
            }
            if (state.equals(StateStatus.REJECTED.name())) {
                return bookingMapper.mapToBookingDtoList(bookingRepository.
                        findByBookerIdAndStatus(userId, BookingStatus.REJECTED, sort));
            } else {
                log.warn("Запросная строка " + state + " некорректна");
                throw new StateException("illegal state");
            }
        } else {
            log.warn("Пользователь с id " + userId + " не найден");
            throw new SearchException("Пользователь с id " + userId + " не найден");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> findAllBookingsByOwner(String state, long userId) {
        if (UserValidator.isThereAUser(userId, userMapper.mapToUsersMap(userRepository.findAll()))) {
            if (itemRepository.findAllByOwner(userRepository.getById(userId)).
                    stream().map(itemMapper::toItemDto).findAny().isPresent()) {
                if (state.equals(StateStatus.ALL.name())) {
                    return bookingMapper.mapToBookingDtoList(bookingRepository.findBookingByItemOwnerId(userId, sort));
                }
                if (state.equals(StateStatus.CURRENT.name())) {
                    return bookingMapper.mapToBookingDtoList(bookingRepository.findBookingsByItemOwnerCurrent(userId));
                }
                if (state.equals(StateStatus.PAST.name())) {
                    return bookingMapper.mapToBookingDtoList(bookingRepository.
                            findBookingByItemOwnerIdAndEndIsBefore(userId, LocalDateTime.now(), sort));
                }
                if (state.equals(StateStatus.FUTURE.name())) {
                    return bookingMapper.mapToBookingDtoList(bookingRepository.
                            findBookingByItemOwnerIdAndStartIsAfter(userId, LocalDateTime.now(), sort));
                }
                if (state.equals(StateStatus.WAITING.name())) {
                    return bookingMapper.mapToBookingDtoList(bookingRepository.
                            findBookingByItemOwnerIdAndStatus(userId, BookingStatus.WAITING, sort));
                }
                if (state.equals(StateStatus.REJECTED.name())) {
                    return bookingMapper.mapToBookingDtoList(bookingRepository.
                            findBookingByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED, sort));
                } else {
                    log.warn("Запросная строка " + state + " некорректна");
                    throw new StateException("illegal state");
                }
            } else {
                log.warn("У пользователя с id " + userId + " нет вещей");
                throw new SearchException("У пользователя с id " + userId + " нет вещей");
            }
        } else {
            log.warn("Пользователь с id " + userId + " не найден");
            throw new SearchException("Пользователь с id " + userId + " не найден");
        }
    }
    private Map<Long, Booking> getBookingsMap() {
        return bookingRepository.findAll().stream().collect(Collectors.toMap(Booking::getId, booking -> booking));
    }
}