package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.BaseTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingResponseDto;
import ru.practicum.shareit.exeptions.BookingNotFoundException;
import ru.practicum.shareit.exeptions.ItemNotFoundException;
import ru.practicum.shareit.exeptions.SearchException;
import ru.practicum.shareit.exeptions.UserNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.Mapper;
import ru.practicum.shareit.paginator.Paginator;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;


import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;


@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest extends BaseTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private Mapper mapper;
    @Mock
    private Paginator<BookingDto> paginator;

    @InjectMocks
    private BookingServiceImpl bookingService;
    private final LocalDateTime NOW = LocalDateTime.of(2019, Month.MAY, 15, 12, 15);
    private final Sort sort = Sort.by("start").descending();


    @Test
    void createBookingTest_WhenSaveBooking_ThenReturnBookingDto() {
        BookingResponseDto bookingResponseDto = createBookingResponseDto();
        long userId = 2L;
        User user = createStandartUser();
        Item item = crateItem();
        item.setOwner(user);
        item.setId(1L);
        Booking booking = createBooking(item, user);
        BookingDto bookingDto = createBookingDto(booking);
        bookingDto.setStatus(WAITING);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(mapper.toBooking(bookingResponseDto)).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(mapper.toBookingDto(booking)).thenReturn(bookingDto);

        BookingDto afterSaveBookingDto = bookingService.createBooking(bookingResponseDto, userId);
        assertEquals(bookingDto.getId(), afterSaveBookingDto.getId());
        assertEquals(afterSaveBookingDto.getStatus(), WAITING);
        verify(bookingRepository).save(booking);
    }

    @Test
    void createBookingTest_WhenSaveBooking_ThenUserException() {
        BookingResponseDto bookingResponseDto = createBookingResponseDto();
        long userId = 2L;
        User user = createStandartUser();
        Item item = crateItem();
        item.setOwner(user);
        item.setId(1L);
        Booking booking = createBooking(item, user);
        BookingDto bookingDto = createBookingDto(booking);
        bookingDto.setStatus(WAITING);

        when(userRepository.findById(userId)).thenThrow(new UserNotFoundException("not found"));

        assertThrows(UserNotFoundException.class, () ->
                bookingService.createBooking(bookingResponseDto, userId));
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void createBookingTest_WhenSaveBooking_ThenItemException() {
        BookingResponseDto bookingResponseDto = createBookingResponseDto();
        long userId = 2L;
        User user = createStandartUser();
        Item item = crateItem();
        item.setOwner(user);
        item.setId(1L);
        Booking booking = createBooking(item, user);
        BookingDto bookingDto = createBookingDto(booking);
        bookingDto.setStatus(WAITING);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenThrow(new ItemNotFoundException("not found"));

        assertThrows(ItemNotFoundException.class, () ->
                bookingService.createBooking(bookingResponseDto, userId));
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void createBookingTest_WhenSaveBooking_ThenBookingNotFoundException() {
        BookingResponseDto bookingResponseDto = createBookingResponseDto();
        long userId = 2L;
        User user = createStandartUser();
        Item item = crateItem();
        item.setOwner(user);
        item.setAvailable(false);
        item.setId(1L);
        Booking booking = createBooking(item, user);
        BookingDto bookingDto = createBookingDto(booking);
        bookingDto.setStatus(WAITING);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(BookingNotFoundException.class, () ->
                bookingService.createBooking(bookingResponseDto, userId));
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void createBookingTest_WhenSaveBooking_ThenIllegalArgumentException() {
        BookingResponseDto bookingResponseDto = createBookingResponseDto();
        bookingResponseDto.setEnd(LocalDateTime.now().minusHours(1L));
        long userId = 2L;
        User user = createStandartUser();
        Item item = crateItem();
        item.setOwner(user);
        item.setId(1L);
        Booking booking = createBooking(item, user);
        BookingDto bookingDto = createBookingDto(booking);
        bookingDto.setStatus(WAITING);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(IllegalArgumentException.class, () ->
                bookingService.createBooking(bookingResponseDto, userId));
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void createBookingTest_WhenSaveBooking_ThenSearchException() {
        BookingResponseDto bookingResponseDto = createBookingResponseDto();
        long userId = 1L;
        User user = createStandartUser();
        Item item = crateItem();
        item.setOwner(user);
        item.setId(1L);
        Booking booking = createBooking(item, user);
        BookingDto bookingDto = createBookingDto(booking);
        bookingDto.setStatus(WAITING);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(mapper.toBooking(bookingResponseDto)).thenReturn(booking);

        assertThrows(SearchException.class, () ->
                bookingService.createBooking(bookingResponseDto, userId));
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void patchBookingTest_WhenPatchBooking_ThenPatchBookingAndReturnBookingDto() {
        long userId = 1L;
        long bookingId = 1L;
        boolean approved = true;
        User user = createStandartUser();
        Item item = crateItem();
        item.setOwner(user);
        item.setId(1L);
        Booking booking = createBooking(item, user);
        booking.setStatus(WAITING);
        BookingDto bookingDto = createBookingDto(booking);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(booking.getItem().getId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(mapper.toBookingDto(booking)).thenReturn(bookingDto);

        bookingService.patchBooking(bookingId, approved, userId);
        verify(bookingRepository).save(booking);
    }

    @Test
    void patchBookingTest_WhenPatchBooking_ThenBookingNotFoundException() {
        long userId = 1L;
        long bookingId = 1L;
        boolean approved = true;
        User user = createStandartUser();
        Item item = crateItem();
        item.setOwner(user);
        item.setId(1L);
        Booking booking = createBooking(item, user);
        booking.setStatus(WAITING);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () ->
                bookingService.patchBooking(bookingId, approved, userId));
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void patchBookingTest_WhenPatchBooking_ThenUserNotFoundException() {
        long userId = 2L;
        long bookingId = 1L;
        boolean approved = true;
        User user = createStandartUser();
        Item item = crateItem();
        item.setOwner(user);
        item.setId(1L);
        Booking booking = createBooking(item, user);
        booking.setStatus(WAITING);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(booking.getItem().getId())).thenReturn(Optional.of(item));

        assertThrows(UserNotFoundException.class, () ->
                bookingService.patchBooking(bookingId, approved, userId));
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void patchBookingTest_WhenPatchBooking_ThenIllegalArgumentException() {
        long userId = 1L;
        long bookingId = 1L;
        boolean approved = true;
        User user = createStandartUser();
        Item item = crateItem();
        item.setOwner(user);
        item.setId(1L);
        Booking booking = createBooking(item, user);
        booking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(booking.getItem().getId())).thenReturn(Optional.of(item));

        assertThrows(IllegalArgumentException.class, () ->
                bookingService.patchBooking(bookingId, approved, userId));
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void findBookingDtoByIdTest_WhenFindBooking_ThenReturnBookingDto() {
        long userId = 1L;
        long bookingId = 1L;
        User user = createStandartUser();
        Item item = crateItem();
        item.setOwner(user);
        item.setId(1L);
        Booking booking = createBooking(item, user);
        booking.setStatus(BookingStatus.APPROVED);
        BookingDto bookingDto = createBookingDto(booking);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(mapper.toBookingDto(booking)).thenReturn(bookingDto);

        BookingDto afterFindBookingDto = bookingService.findBookingDtoById(bookingId, userId);
        assertEquals(afterFindBookingDto.getStatus(), bookingDto.getStatus());
    }

    @Test
    void findBookingDtoByIdTest_WhenFindBooking_ThenBookingNotFoundException() {
        long userId = 1L;
        long bookingId = 1L;
        User user = createStandartUser();
        Item item = crateItem();
        item.setOwner(user);
        item.setId(1L);
        Booking booking = createBooking(item, user);
        booking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () ->
                bookingService.findBookingDtoById(bookingId, userId));
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void findBookingDtoByIdTest_WhenFindBooking_ThenSearchException() {
        long userId = 2L;
        long bookingId = 1L;
        User user = createStandartUser();
        Item item = crateItem();
        item.setOwner(user);
        item.setId(1L);
        Booking booking = createBooking(item, user);
        booking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(SearchException.class, () ->
                bookingService.findBookingDtoById(bookingId, userId));
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void findAllBookingsByBooker_WhenFindBooking_ThenBookingNotFoundException() {
        long userId = 1L;
        String state = "ALL";
        Long from = 0L;
        Long size = 20L;
        when(userRepository.findById(userId)).thenThrow(new UserNotFoundException("not found"));

        assertThrows(UserNotFoundException.class, () ->
                bookingService.findAllBookingsByBooker(state, userId, from, size));
    }

    @Test
    void findAllBookingsByBooker_WhenFindBookings_ThenReturnBookingsDtoList() {
        long userId = 1L;
        String state = "ALL";
        Long from = 0L;
        Long size = 20L;
        List<Booking> bookings = List.of(buildBooking(3L, buildItem(2L, "item", "description", true, buildUser(4L, "mail@mail.com", "user"), null), buildUser(4L, "mail@mail.com", "user"), NOW.plusDays(3), NOW.plusDays(4), WAITING),
                buildBooking(1L, buildItem(2L, "item", "description", true, buildUser(4L, "mail@mail.com", "user"), null), buildUser(4L, "mail@mail.com", "user"), NOW.plusDays(2), NOW.plusDays(4), WAITING),
                buildBooking(2L, buildItem(2L, "item", "description", true, buildUser(4L, "mail@mail.com", "user"), null), buildUser(4L, "mail@mail.com", "user"), NOW.plusDays(1), NOW.plusDays(4), WAITING));

        List<BookingDto> bookingDtoList = List.of(buildBookingDto(3L, buildItem(2L, "item", "description", true, buildUser(4L, "mail@mail.com", "user"), null), buildUser(4L, "mail@mail.com", "user"), NOW.plusDays(3), NOW.plusDays(4), WAITING),
                buildBookingDto(1L, buildItem(2L, "item", "description", true, buildUser(4L, "mail@mail.com", "user"), null), buildUser(4L, "mail@mail.com", "user"), NOW.plusDays(2), NOW.plusDays(4), WAITING),
                buildBookingDto(2L, buildItem(2L, "item", "description", true, buildUser(4L, "mail@mail.com", "user"), null), buildUser(4L, "mail@mail.com", "user"), NOW.plusDays(1), NOW.plusDays(4), WAITING));

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findByBookerId(userId, sort)).thenReturn(bookings);
        when(mapper.mapToBookingDtoList(bookings)).thenReturn(bookingDtoList);
        when(paginator.paginationOf(bookingDtoList, from, size)).thenReturn(bookingDtoList);

        List<BookingDto> responseBookingDtoList = bookingService.findAllBookingsByBooker(state, userId, from, size);

        verify(bookingRepository).findByBookerId(userId, sort);
        verify(bookingRepository, never()).findByBookerIdCurrent(userId);
        verify(bookingRepository, never()).findByBookerIdAndEndIsBefore(userId, LocalDateTime.now(), sort);
        verify(bookingRepository, never()).findByBookerIdAndStartIsAfter(userId, LocalDateTime.now(), sort);
        verify(bookingRepository, never()).findByBookerIdAndStatus(userId, BookingStatus.WAITING, sort);
        verify(bookingRepository, never()).findByBookerIdAndStatus(userId, BookingStatus.REJECTED, sort);

        assertEquals(responseBookingDtoList.size(), 3);
    }

    @Test
    void findAllBookingsByBooker_WhenFindBookings_ThenReturnByBookerIdCurrent() {
        long userId = 1L;
        String state = "CURRENT";
        Long from = 0L;
        Long size = 20L;
        List<Booking> bookings = List.of(buildBooking(3L, buildItem(2L, "item", "description", true, buildUser(4L, "mail@mail.com", "user"), null), buildUser(4L, "mail@mail.com", "user"), NOW.plusDays(3), NOW.plusDays(4), WAITING),
                buildBooking(1L, buildItem(2L, "item", "description", true, buildUser(4L, "mail@mail.com", "user"), null), buildUser(4L, "mail@mail.com", "user"), NOW.plusDays(2), NOW.plusDays(4), WAITING),
                buildBooking(2L, buildItem(2L, "item", "description", true, buildUser(4L, "mail@mail.com", "user"), null), buildUser(4L, "mail@mail.com", "user"), NOW.plusDays(1), NOW.plusDays(4), WAITING));

        List<BookingDto> bookingDtoList = List.of(buildBookingDto(3L, buildItem(2L, "item", "description", true, buildUser(4L, "mail@mail.com", "user"), null), buildUser(4L, "mail@mail.com", "user"), NOW.plusDays(3), NOW.plusDays(4), WAITING),
                buildBookingDto(1L, buildItem(2L, "item", "description", true, buildUser(4L, "mail@mail.com", "user"), null), buildUser(4L, "mail@mail.com", "user"), NOW.plusDays(2), NOW.plusDays(4), WAITING),
                buildBookingDto(2L, buildItem(2L, "item", "description", true, buildUser(4L, "mail@mail.com", "user"), null), buildUser(4L, "mail@mail.com", "user"), NOW.plusDays(1), NOW.plusDays(4), WAITING));

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findByBookerIdCurrent(userId)).thenReturn(bookings);
        when(mapper.mapToBookingDtoList(bookings)).thenReturn(bookingDtoList);
        when(paginator.paginationOf(bookingDtoList, from, size)).thenReturn(bookingDtoList);

        List<BookingDto> responseBookingDtoList = bookingService.findAllBookingsByBooker(state, userId, from, size);

        verify(bookingRepository, never()).findByBookerId(userId, sort);
        verify(bookingRepository).findByBookerIdCurrent(userId);
        verify(bookingRepository, never()).findByBookerIdAndEndIsBefore(userId, LocalDateTime.now(), sort);
        verify(bookingRepository, never()).findByBookerIdAndStartIsAfter(userId, LocalDateTime.now(), sort);
        verify(bookingRepository, never()).findByBookerIdAndStatus(userId, BookingStatus.WAITING, sort);
        verify(bookingRepository, never()).findByBookerIdAndStatus(userId, BookingStatus.REJECTED, sort);

        assertEquals(responseBookingDtoList.size(), 3);
    }

    @Test
    void findAllBookingsByBooker_WhenFindBookings_ThenReturnByBookerIdAndEndIsBefore() {
        long userId = 1L;
        String state = "PAST";
        Long from = 0L;
        Long size = 20L;
        List<Booking> bookings = List.of(buildBooking(3L, buildItem(2L, "item", "description", true, buildUser(4L, "mail@mail.com", "user"), null), buildUser(4L, "mail@mail.com", "user"), NOW.plusDays(3), NOW.plusDays(4), WAITING),
                buildBooking(1L, buildItem(2L, "item", "description", true, buildUser(4L, "mail@mail.com", "user"), null), buildUser(4L, "mail@mail.com", "user"), NOW.plusDays(2), NOW.plusDays(4), WAITING),
                buildBooking(2L, buildItem(2L, "item", "description", true, buildUser(4L, "mail@mail.com", "user"), null), buildUser(4L, "mail@mail.com", "user"), NOW.plusDays(1), NOW.plusDays(4), WAITING));

        List<BookingDto> bookingDtoList = List.of(buildBookingDto(3L, buildItem(2L, "item", "description", true, buildUser(4L, "mail@mail.com", "user"), null), buildUser(4L, "mail@mail.com", "user"), NOW.plusDays(3), NOW.plusDays(4), WAITING),
                buildBookingDto(1L, buildItem(2L, "item", "description", true, buildUser(4L, "mail@mail.com", "user"), null), buildUser(4L, "mail@mail.com", "user"), NOW.plusDays(2), NOW.plusDays(4), WAITING),
                buildBookingDto(2L, buildItem(2L, "item", "description", true, buildUser(4L, "mail@mail.com", "user"), null), buildUser(4L, "mail@mail.com", "user"), NOW.plusDays(1), NOW.plusDays(4), WAITING));

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        lenient().when(bookingRepository.findByBookerIdAndEndIsBefore(userId, NOW, sort)).thenReturn(bookings);
        when(mapper.mapToBookingDtoList(anyList())).thenReturn(bookingDtoList);
        when(paginator.paginationOf(bookingDtoList, from, size)).thenReturn(bookingDtoList);

        List<BookingDto> responseBookingDtoList = bookingService.findAllBookingsByBooker(state, userId, from, size);

        verify(bookingRepository, never()).findByBookerId(userId, sort);
        verify(bookingRepository, never()).findByBookerIdCurrent(userId);
        verify(bookingRepository, never()).findByBookerIdAndStartIsAfter(userId, LocalDateTime.now(), sort);
        verify(bookingRepository, never()).findByBookerIdAndStatus(userId, BookingStatus.WAITING, sort);
        verify(bookingRepository, never()).findByBookerIdAndStatus(userId, BookingStatus.REJECTED, sort);

        assertEquals(responseBookingDtoList.size(), 3);
    }
}