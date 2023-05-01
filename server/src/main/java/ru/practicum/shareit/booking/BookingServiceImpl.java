package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.StateStatus;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingResponseDto;
import ru.practicum.shareit.exeptions.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.Mapper;
import ru.practicum.shareit.paginator.Paginator;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;


import java.time.LocalDateTime;
import java.util.List;


@Validated
@Service
@Slf4j

public class BookingServiceImpl implements BookingService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final Mapper mapper;
    private final Sort sort = Sort.by("start").descending();
    private final Paginator<BookingDto> paginator;

    @Autowired
    public BookingServiceImpl(ItemRepository itemRepository, UserRepository userRepository,
                              BookingRepository bookingRepository, Mapper mapper, Paginator<BookingDto> paginator) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.mapper = mapper;
        this.paginator = paginator;
    }


    @Override
    public BookingDto createBooking(@Validated BookingResponseDto responseDto, long userId) {
        User booker = userRepository.findById(userId).
                orElseThrow(() -> new UserNotFoundException("Пользователя с id = " + userId + " не найден"));
        Item item = itemRepository.findById(responseDto.getItemId()).orElseThrow(() ->
                new ItemNotFoundException("Вещь с id = " + responseDto.getItemId() + " не найдена"));
                if (item.getAvailable()) {
                    if (!responseDto.getEnd().isAfter(responseDto.getStart())) {
                        log.warn("Ошибка валидации даты/времени");
                        throw new IllegalArgumentException("Ошибка валидации даты/времени");
                    }
                    Booking booking = mapper.toBooking(responseDto);
                    booking.setItem(item);
                    booking.setBooker(booker);
                    booking.setStatus(BookingStatus.WAITING);
                    if (userId == booking.getItem().getOwner().getId()) {
                        throw new SearchException("Пользователь не может сам у себя бронировать вещи");
                    }
                    log.info("Бронирование зарегестрированно");
                    return mapper.toBookingDto(bookingRepository.save(booking));
                } else {
                    log.warn("Вещь с id " + responseDto.getItemId() + " недоступна для заказа");
                    throw new BookingNotFoundException("Вещь с id " + responseDto.getItemId() + " недоступна для заказа");
                }
    }

    public BookingDto patchBooking(long bookingId, boolean approved, long userId) {
        Booking booking = bookingRepository.findById(bookingId).
                orElseThrow(() -> new BookingNotFoundException("Бронирования с id " + bookingId + " не найдено"));
        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow(()
                -> new ItemNotFoundException("Вещь с id = " + booking.getItem().getId() + " не найдена"));
            if (item.getOwner().getId().equals(userId)) {
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
                log.info("Бронирование обновлено");
                return mapper.toBookingDto(bookingRepository.save(booking));
            } else {
                log.warn("Пользователь с id " + userId + " не найден");
                throw new UserNotFoundException("Пользователь с id " + userId + " не найден");
            }
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto findBookingDtoById(long bookingId, long userId) {
            Booking booking = bookingRepository.findById(bookingId).
                    orElseThrow(() -> new BookingNotFoundException("Бронирования с id " + bookingId + " не найдено"));
            if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
                log.info("Бронирование id = " + bookingId + " отправленно пользователю id = " + userId);
                return mapper.toBookingDto(booking);
            } else {
                log.warn("Пользователь с id " + userId + " не имеет доступа к данному бронированию");
                throw new SearchException("Пользователь с id " + userId + " не имеет доступа к данному бронированию");
            }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> findAllBookingsByBooker(String state, long userId, Long from, Long size) {
        userRepository.findById(userId).
                orElseThrow(() -> new UserNotFoundException("Пользователя с id = " + userId + " не найден"));
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
        User owner = userRepository.findById(userId).
                orElseThrow(() -> new UserNotFoundException("Пользователя с id = " + userId + " не найден"));
            if (itemRepository.findAllByOwner(owner).
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