package ru.yandex.practicum.shareIt.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.shareIt.mapper.Mapper;
import ru.yandex.practicum.shareIt.booking.model.*;
import ru.yandex.practicum.shareIt.exeptions.SearchException;
import ru.yandex.practicum.shareIt.exeptions.StateException;
import ru.yandex.practicum.shareIt.exeptions.handlers.BookingNotFoundException;
import ru.yandex.practicum.shareIt.item.ItemService;
import ru.yandex.practicum.shareIt.item.model.Item;
import ru.yandex.practicum.shareIt.paginator.Paginator;
import ru.yandex.practicum.shareIt.user.UserService;
import ru.yandex.practicum.shareIt.user.model.User;

import java.time.LocalDateTime;
import java.util.List;


@Validated
@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final Mapper mapper;
    private final Sort sort = Sort.by("start").descending();
    private final Paginator<BookingDto> paginator;


    @Override
    public BookingDto createBooking(@Validated BookingResponseDto responseDto, long userId) {
        User booker = userService.findUserById(userId);
        Item item = itemService.findItemById(responseDto.getItemId());
                if (item.getAvailable()) {
                    if (!responseDto.getEnd().isAfter(responseDto.getStart())) {
                        log.warn("Ошибка валидации даты/времени");
                        throw new IllegalArgumentException("Ошибка валидации даты/времени");
                    }
                    Booking booking = mapper.toBooking(responseDto);
                    booking.setItem(item);
                    booking.setBooker(booker);
                    booking.setStatus(BookingStatus.WAITING);
                    if (userId == booking.getItem().getId()) {
                        throw new SearchException("Пользователь не может сам у себя бронировать вещи");
                    }
                    log.info("Бронирование зарегестрированно");
                    return mapper.toBookingDto(bookingRepository.save(booking));
                } else {
                    log.warn("Вещь с id " + responseDto.getItemId() + " недоступна для заказа");
                    throw new IllegalArgumentException("Вещь с id " + responseDto.getItemId() + " недоступна для заказа");
                }
    }

    public BookingDto patchBooking(long bookingId, boolean approved, long userId) {
        Booking booking = findBookingById(bookingId);
            if (itemService.findItemById(booking.getItem().getId()).getOwner().getId().equals(userId)) {
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
                return mapper.toBookingDto(booking);
            } else {
                log.warn("Пользователь с id " + userId + " не найден");
                throw new SearchException("Пользователь с id " + userId + " не найден");
            }
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto findBookingDtoById(long bookingId, long userId) {
            Booking booking = findBookingById(bookingId);
            if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
                log.info("Бронирование id = " + bookingId + " отправленно пользователю id = " + userId);
                return mapper.toBookingDto(booking);
            } else {
                log.warn("Пользователь с id " + userId + " не имеет доступа к данному бронированию");
                throw new SearchException("Пользователь с id " + userId + " не имеет доступа к данному бронированию");
            }
    }

    @Override
    public Booking findBookingById(long bookingId) {
        return bookingRepository.findById(bookingId).
                orElseThrow(() -> new BookingNotFoundException("Бронирования с id " + bookingId + " не найдено"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> findAllBookingsByBooker(String state, long userId, Long from, Long size) {
        userService.findUserById(userId);
            if (state.equals(StateStatus.ALL.name())) {
                return paginator.paginationOf(mapper.
                        mapToBookingDtoList(bookingRepository.findByBookerId(userId, sort)), from, size);
            }
            if (state.equals(StateStatus.CURRENT.name())) {
                return paginator.paginationOf(mapper.
                        mapToBookingDtoList(bookingRepository.findByBookerIdCurrent(userId)), from, size);
            }
            if (state.equals(StateStatus.PAST.name())) {
                return paginator.paginationOf(mapper.mapToBookingDtoList(bookingRepository.
                        findByBookerIdAndEndIsBefore(userId, LocalDateTime.now(), sort)), from, size);
            }
            if (state.equals(StateStatus.FUTURE.name())) {
                return paginator.paginationOf(mapper.mapToBookingDtoList(bookingRepository.
                        findByBookerIdAndStartIsAfter(userId, LocalDateTime.now(), sort)), from, size);
            }
            if (state.equals(StateStatus.WAITING.name())) {
                return paginator.paginationOf(mapper.mapToBookingDtoList(bookingRepository.
                        findByBookerIdAndStatus(userId, BookingStatus.WAITING, sort)), from, size);
            }
            if (state.equals(StateStatus.REJECTED.name())) {
                return paginator.paginationOf(mapper.mapToBookingDtoList(bookingRepository.
                        findByBookerIdAndStatus(userId, BookingStatus.REJECTED, sort)), from, size);
            } else {
                log.warn("Запросная строка " + state + " некорректна");
                throw new StateException("illegal state");
            }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> findAllBookingsByOwner(String state, long userId, Long from, Long size) {
        User owner = userService.findUserById(userId);
            if (itemService.findAllByOwner(owner).
                    stream().map(mapper::toItemDto).findAny().isPresent()) {
                if (state.equals(StateStatus.ALL.name())) {
                    return paginator.paginationOf(mapper.
                            mapToBookingDtoList(bookingRepository.findBookingByItemOwnerId(userId, sort)), from, size);
                }
                if (state.equals(StateStatus.CURRENT.name())) {
                    return paginator.paginationOf(mapper.
                            mapToBookingDtoList(bookingRepository.findBookingsByItemOwnerCurrent(userId)), from, size);
                }
                if (state.equals(StateStatus.PAST.name())) {
                    return paginator.paginationOf(mapper.mapToBookingDtoList(bookingRepository.
                            findBookingByItemOwnerIdAndEndIsBefore(userId, LocalDateTime.now(), sort)), from, size);
                }
                if (state.equals(StateStatus.FUTURE.name())) {
                    return paginator.paginationOf(mapper.mapToBookingDtoList(bookingRepository.
                            findBookingByItemOwnerIdAndStartIsAfter(userId, LocalDateTime.now(), sort)), from, size);
                }
                if (state.equals(StateStatus.WAITING.name())) {
                    return paginator.paginationOf(mapper.mapToBookingDtoList(bookingRepository.
                            findBookingByItemOwnerIdAndStatus(userId, BookingStatus.WAITING, sort)), from, size);
                }
                if (state.equals(StateStatus.REJECTED.name())) {
                    return paginator.paginationOf(mapper.mapToBookingDtoList(bookingRepository.
                            findBookingByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED, sort)), from, size);
                } else {
                    log.warn("Запросная строка " + state + " некорректна");
                    throw new StateException("illegal state");
                }
            } else {
                log.warn("У пользователя с id " + userId + " нет вещей");
                throw new SearchException("У пользователя с id " + userId + " нет вещей");
            }
    }
}