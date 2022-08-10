package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private ItemRepository mockItemRepository;
    @Mock
    private BookingRepository mockBookingRepository;

    @Test
    void addBookingRequest() {
        Item item = new Item();
        item.setId(1);
        item.setName("name");
        item.setDescription("description");
        User owner = new User();
        owner.setId(1);
        item.setOwner(owner);
        item.setAvailable(true);

        User booker = new User();
        booker.setId(2);

        Mockito.when(mockUserRepository.findById(Mockito.eq(2L))).thenReturn(Optional.of(booker));
        Mockito.when(mockItemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));

        Mockito.when(mockBookingRepository.save(Mockito.any(Booking.class)))
                .thenAnswer((Answer<Booking>) invocationOnMock -> {
                    Booking savedBooking = invocationOnMock.getArgument(0);
                    savedBooking.setId(1);
                    return savedBooking;
                });

        BookingDtoInput booking = new BookingDtoInput();
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now());
        booking.setItemId(1L);
        BookingDtoOutput result = bookingService.addBookingRequest(2L, booking);
        assertEquals(1L, result.getId());
        assertEquals(item.getId(), result.getItem().getId());
        assertEquals(BookingStatus.WAITING, result.getStatus());
        assertEquals(booker.getId(), result.getBooker().getId());
    }

    @Test
    void addBookingRequestUnrealisticDate() {
        BookingDtoInput booking = new BookingDtoInput();
        booking.setEnd(LocalDateTime.now());
        booking.setStart(LocalDateTime.now());

        assertThrows(ResponseStatusException.class,
                () -> bookingService.addBookingRequest(2L, booking));
    }

    @Test
    void addBookingRequestItemNotAvailable() {
        Item item = new Item();
        item.setId(1);
        item.setName("name");
        item.setDescription("description");
        User owner = new User();
        owner.setId(1);
        item.setOwner(owner);
        item.setAvailable(false);

        User booker = new User();
        booker.setId(2);

        Mockito.when(mockUserRepository.findById(Mockito.eq(2L))).thenReturn(Optional.of(booker));
        Mockito.when(mockItemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));

        BookingDtoInput booking = new BookingDtoInput();
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now());
        booking.setItemId(1L);
        assertThrows(ResponseStatusException.class,
                () -> bookingService.addBookingRequest(2L, booking));
    }

    @Test
    void addBookingRequestItemOwnItem() {
        Item item = new Item();
        item.setId(1);
        item.setName("name");
        item.setDescription("description");
        User owner = new User();
        owner.setId(1);
        item.setOwner(owner);
        item.setAvailable(false);

        Mockito.when(mockUserRepository.findById(Mockito.eq(1L))).thenReturn(Optional.of(owner));
        Mockito.when(mockItemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));

        BookingDtoInput booking = new BookingDtoInput();
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now());
        booking.setItemId(1L);
        assertThrows(ResponseStatusException.class,
                () -> bookingService.addBookingRequest(1L, booking));
    }

    @Test
    void approveOrRejectBookingApproved() {
        User owner = new User();
        owner.setId(1);
        Item item = new Item();
        item.setId(1);
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.WAITING);
        User booker = new User();
        booker.setId(2);
        booking.setBooker(booker);
        booking.setItem(item);

        Mockito.when(mockUserRepository.findById(Mockito.eq(1L))).thenReturn(Optional.of(owner));
        Mockito.when(mockBookingRepository.findById(Mockito.eq(1L))).thenReturn(Optional.of(booking));

        BookingDtoOutput result = bookingService.approveOrRejectBooking(booking.getId(), true, owner.getId());
        assertEquals(BookingStatus.APPROVED, result.getStatus());
        Mockito.verify(mockBookingRepository, Mockito.times(1)).save(Mockito.any(Booking.class));
    }

    @Test
    void approveOrRejectBookingRejected() {
        User owner = new User();
        owner.setId(1);
        Item item = new Item();
        item.setId(1);
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.WAITING);
        User booker = new User();
        booker.setId(2);
        booking.setBooker(booker);
        booking.setItem(item);

        Mockito.when(mockUserRepository.findById(Mockito.eq(1L))).thenReturn(Optional.of(owner));
        Mockito.when(mockBookingRepository.findById(Mockito.eq(1L))).thenReturn(Optional.of(booking));

        BookingDtoOutput result = bookingService.approveOrRejectBooking(booking.getId(), false, owner.getId());
        assertEquals(BookingStatus.REJECTED, result.getStatus());
        Mockito.verify(mockBookingRepository, Mockito.times(1)).save(Mockito.any(Booking.class));
    }

    @Test
    void approveOrRejectBookingNotWaiting() {
        User owner = new User();
        owner.setId(1);
        Item item = new Item();
        item.setId(1);
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setId(1L);
        User booker = new User();
        booker.setId(2);
        booking.setBooker(booker);
        booking.setItem(item);

        Mockito.when(mockUserRepository.findById(Mockito.eq(1L))).thenReturn(Optional.of(owner));
        Mockito.when(mockBookingRepository.findById(Mockito.eq(1L))).thenReturn(Optional.of(booking));

        assertThrows(ResponseStatusException.class,
                () -> bookingService.approveOrRejectBooking(booking.getId(), true, owner.getId()));
    }

    @Test
    void approveOrRejectBookingRequesterIsBooker() {
        User owner = new User();
        owner.setId(1);
        Item item = new Item();
        item.setId(1);
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setId(1L);
        User booker = new User();
        booker.setId(2);
        booking.setBooker(booker);
        booking.setItem(item);

        Mockito.when(mockUserRepository.findById(Mockito.eq(1L))).thenReturn(Optional.of(owner));
        Mockito.when(mockBookingRepository.findById(Mockito.eq(1L))).thenReturn(Optional.of(booking));

        assertThrows(ResponseStatusException.class,
                () -> bookingService.approveOrRejectBooking(booking.getId(), true, booker.getId()));
    }

    @Test
    void approveOrRejectBookingRequesterIsNotOwner() {
        User owner = new User();
        owner.setId(1);
        Item item = new Item();
        item.setId(1);
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setId(1L);
        User booker = new User();
        booker.setId(2);
        booking.setBooker(booker);
        booking.setItem(item);

        Mockito.when(mockUserRepository.findById(Mockito.eq(1L))).thenReturn(Optional.of(owner));
        Mockito.when(mockBookingRepository.findById(Mockito.eq(1L))).thenReturn(Optional.of(booking));

        assertThrows(ResponseStatusException.class,
                () -> bookingService.approveOrRejectBooking(booking.getId(), true, 333L));
    }

    @Test
    void getBookingByIdRequesterBooker() {
        User owner = new User();
        owner.setId(1);
        Item item = new Item();
        item.setId(1);
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.WAITING);
        User booker = new User();
        booker.setId(2);
        booking.setBooker(booker);
        booking.setItem(item);

        Mockito.when(mockUserRepository.findById(Mockito.eq(1L))).thenReturn(Optional.of(owner));
        Mockito.when(mockUserRepository.findById(Mockito.eq(2L))).thenReturn(Optional.of(booker));
        Mockito.when(mockBookingRepository.findById(Mockito.eq(1L))).thenReturn(Optional.of(booking));

        BookingDtoOutput result = bookingService.getBookingById(booking.getId(), booker.getId());
        assertEquals(booking.getId(), result.getId());
    }

    @Test
    void getBookingByIdRequesterOwner() {
        User owner = new User();
        owner.setId(1);
        Item item = new Item();
        item.setId(1);
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.WAITING);
        User booker = new User();
        booker.setId(2);
        booking.setBooker(booker);
        booking.setItem(item);

        Mockito.when(mockUserRepository.findById(Mockito.eq(1L))).thenReturn(Optional.of(owner));
        Mockito.when(mockUserRepository.findById(Mockito.eq(2L))).thenReturn(Optional.of(booker));
        Mockito.when(mockBookingRepository.findById(Mockito.eq(1L))).thenReturn(Optional.of(booking));

        BookingDtoOutput result = bookingService.getBookingById(booking.getId(), owner.getId());
        assertEquals(booking.getId(), result.getId());
    }

    @Test
    void getBookingByIdRequesterUnknown() {
        User owner = new User();
        owner.setId(1);
        Item item = new Item();
        item.setId(1);
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.WAITING);
        User booker = new User();
        booker.setId(2);
        booking.setBooker(booker);
        booking.setItem(item);

        Mockito.when(mockUserRepository.findById(Mockito.eq(1L))).thenReturn(Optional.of(owner));
        Mockito.when(mockUserRepository.findById(Mockito.eq(2L))).thenReturn(Optional.of(booker));
        Mockito.when(mockBookingRepository.findById(Mockito.eq(1L))).thenReturn(Optional.of(booking));

        assertThrows(ResponseStatusException.class,
                () -> bookingService.getBookingById(booking.getId(), 3L));
    }

    @Test
    void getAllBookingsOfUserAll() {
        User booker = new User();
        booker.setId(2);
        Mockito.when(mockUserRepository.findById(Mockito.eq(2L))).thenReturn(Optional.of(booker));

        bookingService.getAllBookingsOfUser(booker.getId(), BookingStatus.ALL, 0, 5);
        Mockito.verify(mockBookingRepository, Mockito.times(1))
                .findBookingsByBookerIdOrderByStartDesc(Mockito.eq(booker.getId()), Mockito.any(PageRequest.class));
    }

    @Test
    void getAllBookingsOfUserPast() {
        User booker = new User();
        booker.setId(2);
        Mockito.when(mockUserRepository.findById(Mockito.eq(2L))).thenReturn(Optional.of(booker));

        bookingService.getAllBookingsOfUser(booker.getId(), BookingStatus.PAST, 0, 5);
        Mockito.verify(mockBookingRepository, Mockito.times(1))
                .findBookingsByBookerIdAndEndBeforeOrderByStartDesc(
                        Mockito.eq(booker.getId()), Mockito.any(), Mockito.any(PageRequest.class));
    }

    @Test
    void getAllBookingsOfUserFuture() {
        User booker = new User();
        booker.setId(2);
        Mockito.when(mockUserRepository.findById(Mockito.eq(2L))).thenReturn(Optional.of(booker));

        bookingService.getAllBookingsOfUser(booker.getId(), BookingStatus.FUTURE, 0, 5);
        Mockito.verify(mockBookingRepository, Mockito.times(1))
                .findBookingsByBookerIdAndStartAfterOrderByStartDesc(
                        Mockito.eq(booker.getId()), Mockito.any(), Mockito.any(PageRequest.class));
    }

    @Test
    void getAllBookingsOfUserCurrent() {
        User booker = new User();
        booker.setId(2);
        Mockito.when(mockUserRepository.findById(Mockito.eq(2L))).thenReturn(Optional.of(booker));

        bookingService.getAllBookingsOfUser(booker.getId(), BookingStatus.CURRENT, 0, 5);
        Mockito.verify(mockBookingRepository, Mockito.times(1))
                .findBookingsByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        Mockito.eq(booker.getId()), Mockito.any(), Mockito.any(), Mockito.any(PageRequest.class));
    }

    @Test
    void getAllBookingsOfUserWaiting() {
        User booker = new User();
        booker.setId(2);
        Mockito.when(mockUserRepository.findById(Mockito.eq(2L))).thenReturn(Optional.of(booker));

        bookingService.getAllBookingsOfUser(booker.getId(), BookingStatus.WAITING, 0, 5);
        Mockito.verify(mockBookingRepository, Mockito.times(1))
                .findBookingsByBookerIdAndStatusOrderByStartDesc(
                        Mockito.eq(booker.getId()), Mockito.eq(BookingStatus.WAITING), Mockito.any(PageRequest.class));
    }

    @Test
    void getAllBookingsOfUserReject() {
        User booker = new User();
        booker.setId(2);
        Mockito.when(mockUserRepository.findById(Mockito.eq(2L))).thenReturn(Optional.of(booker));

        bookingService.getAllBookingsOfUser(booker.getId(), BookingStatus.REJECTED, 0, 5);
        Mockito.verify(mockBookingRepository, Mockito.times(1))
                .findBookingsByBookerIdAndStatusOrderByStartDesc(
                        Mockito.eq(booker.getId()), Mockito.eq(BookingStatus.REJECTED), Mockito.any(PageRequest.class));
    }

}