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


    @Override
    public BookingDto createBooking(@Validated BookingDto bookingDto, long userId) {
        if (UserValidator.isThereAUser(userId, UserMapper.mapToUsersMap(userRepository.findAll()))) {
            if (itemRepository.findAll().stream().collect(Collectors.toMap(Item::getId, item -> item)).containsKey(bookingDto.getItemId())) {
                if (itemRepository.getById(bookingDto.getItemId()).getAvailable()) {
                    if (!bookingDto.getEnd().isAfter(bookingDto.getStart())) {
                        log.warn("Ошибка валидации даты/времени");
                        throw new IllegalArgumentException("Ошибка валидации даты/времени");
                    }
                    bookingDto.setItem(itemRepository.getById(bookingDto.getItemId()));
                    bookingDto.setBooker(userRepository.getById(userId));
                    bookingDto.setStatus(BookingStatus.WAITING);
                    if (userId == bookingDto.getItem().getId()) {
                        throw new SearchException("Пользователь не может сам у себя бронировать вещи");
                    }
                    Booking booking = bookingRepository.save(BookingMapper.mapToBooking(bookingDto));
                    log.info("Бронирование зарегестрированно");
                    return BookingMapper.mapToBookingDto(booking);
                } else {
                    log.warn("Вещь с id " + bookingDto.getItemId() + " недоступна для заказа");
                    throw new IllegalArgumentException("Вещь с id " + bookingDto.getItemId() + " недоступна для заказа");
                }
            } else {
                log.warn("Вещь с id " + bookingDto.getItemId() + " не найдена");
                throw new SearchException("Вещь с id " + bookingDto.getItemId() + " не найдена");
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
                return BookingMapper.mapToBookingDto(booking);
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
                return BookingMapper.mapToBookingDto(booking);
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
        if (UserValidator.isThereAUser(userId, UserMapper.mapToUsersMap(userRepository.findAll()))) {
            if (state.equals(StateStatus.ALL.name())) {
                return BookingMapper.mapToBookingDtoList(bookingRepository.findByBookerId(userId, sort));
            }
            if (state.equals(StateStatus.CURRENT.name())) {
                return BookingMapper.mapToBookingDtoList(bookingRepository.findByBookerIdCurrent(userId));
            }
            if (state.equals(StateStatus.PAST.name())) {
                return BookingMapper.mapToBookingDtoList(bookingRepository.findByBookerIdAndEndIsBefore(userId, LocalDateTime.now(), sort));
            }
            if (state.equals(StateStatus.FUTURE.name())) {
                return BookingMapper.mapToBookingDtoList(bookingRepository.findByBookerIdAndStartIsAfter(userId, LocalDateTime.now(), sort));
            }
            if (state.equals(StateStatus.WAITING.name())) {
                return BookingMapper.mapToBookingDtoList(bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING, sort));
            }
            if (state.equals(StateStatus.REJECTED.name())) {
                return BookingMapper.mapToBookingDtoList(bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, sort));
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
        if (UserValidator.isThereAUser(userId, UserMapper.mapToUsersMap(userRepository.findAll()))) {
            if (itemRepository.findAllByOwner(userRepository.getById(userId)).
                    stream().map(ItemMapper::mapToItemDto).findAny().isPresent()) {
                if (state.equals(StateStatus.ALL.name())) {
                    return BookingMapper.mapToBookingDtoList(bookingRepository.findBookingByItemOwnerId(userId, sort));
                }
                if (state.equals(StateStatus.CURRENT.name())) {
                    return BookingMapper.mapToBookingDtoList(bookingRepository.findBookingsByItemOwnerCurrent(userId));
                }
                if (state.equals(StateStatus.PAST.name())) {
                    return BookingMapper.mapToBookingDtoList(bookingRepository.
                            findBookingByItemOwnerIdAndEndIsBefore(userId, LocalDateTime.now(), sort));
                }
                if (state.equals(StateStatus.FUTURE.name())) {
                    return BookingMapper.mapToBookingDtoList(bookingRepository.
                            findBookingByItemOwnerIdAndStartIsAfter(userId, LocalDateTime.now(), sort));
                }
                if (state.equals(StateStatus.WAITING.name())) {
                    return BookingMapper.mapToBookingDtoList(bookingRepository.
                            findBookingByItemOwnerIdAndStatus(userId, BookingStatus.WAITING, sort));
                }
                if (state.equals(StateStatus.REJECTED.name())) {
                    return BookingMapper.mapToBookingDtoList(bookingRepository.
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