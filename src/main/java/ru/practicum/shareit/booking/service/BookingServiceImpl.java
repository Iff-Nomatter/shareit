package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private final LocalDateTime currentTime = LocalDateTime.now();

    @Override
    public BookingDtoOutput addBookingRequest(long bookerId, BookingDtoInput bookingDtoInput) {
        if (bookingDtoInput.getStart().isAfter(bookingDtoInput.getEnd()) ||
                bookingDtoInput.getStart().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Время аренды не укладывается в реалистичные рамки.");
        }
        User user = getUserOrThrow(bookerId);
        Item item = getItemOrThrow(bookingDtoInput.getItemId());
        if (!item.getAvailable()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Предмет недоступен для аренды.");
        }
        if (item.getOwner().getId() == (user.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Нельзя арендовать свой предмет.");
        }
        Booking booking = BookingMapper.dtoToBooking(bookingDtoInput);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);
        return BookingMapper.toBookingDtoOutput(booking);
    }

    @Override
    public BookingDtoOutput approveOrRejectBooking(long bookingId, boolean isApproved, long requesterId) {
        Booking booking = getBookingOrThrow(bookingId);
        User user = getUserOrThrow(booking.getItem().getOwner().getId());
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Статус аренды уже изменен.");
        }
        if (requesterId == booking.getBooker().getId() || requesterId != booking.getItem().getOwner().getId()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Это не ваш предмет.");
        }
        booking.setStatus(isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        bookingRepository.save(booking);
        return BookingMapper.toBookingDtoOutput(booking);
    }

    @Override
    public BookingDtoOutput getBookingById(long bookingId, long requesterId) {
        Booking booking = getBookingOrThrow(bookingId);
        User booker = getUserOrThrow(booking.getBooker().getId());
        User owner = getUserOrThrow(booking.getItem().getOwner().getId());
        if (requesterId != booking.getBooker().getId() &&
                requesterId != booking.getItem().getOwner().getId()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Это не вашe.");
        }
        return BookingMapper.toBookingDtoOutput(booking);
    }

    @Override
    public List<BookingDtoOutput> getAllBookingsOfUser(long bookerId, BookingStatus status) {
        getUserOrThrow(bookerId);

        switch (status) {
            case ALL:
                return BookingMapper.convertBookingToDtoOutput(bookingRepository
                        .findBookingsByBookerIdOrderByStartDesc(bookerId));
            case PAST:
                return BookingMapper.convertBookingToDtoOutput(bookingRepository
                        .findBookingsByBookerIdAndEndBeforeOrderByStartDesc(
                                bookerId, currentTime));
            case FUTURE:
                return BookingMapper.convertBookingToDtoOutput(bookingRepository
                        .findBookingsByBookerIdAndStartAfterOrderByStartDesc(
                                bookerId, currentTime));
            case CURRENT:
                return BookingMapper.convertBookingToDtoOutput(bookingRepository
                        .findBookingsByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                                bookerId, currentTime, currentTime));
            case WAITING:
                return BookingMapper.convertBookingToDtoOutput(bookingRepository
                        .findBookingsByBookerIdAndStatusOrderByStartDesc(
                                bookerId, BookingStatus.WAITING));
            case REJECTED:
                return BookingMapper.convertBookingToDtoOutput(bookingRepository
                        .findBookingsByBookerIdAndStatusOrderByStartDesc(
                                bookerId, BookingStatus.REJECTED));
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Что-то пошло не так.");
        }
    }

    @Override
    public List<BookingDtoOutput> getAllItemBookingsOfUser(long ownerId, BookingStatus status) {
        getUserOrThrow(ownerId);

        List<Item> allItemsOfOwner = new ArrayList<>(itemRepository.findItemsByOwnerId(ownerId));
        if (allItemsOfOwner.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> allItemsOfOwnerIds = new ArrayList<>();
        for (Item item : allItemsOfOwner) {
            allItemsOfOwnerIds.add(item.getId());
        }

        switch (status) {
            case ALL:
                return BookingMapper.convertBookingToDtoOutput(bookingRepository
                        .findBookingsByItemIdInOrderByStartDesc(
                                allItemsOfOwnerIds));
            case PAST:
                return BookingMapper.convertBookingToDtoOutput(bookingRepository
                        .findBookingsByItemIdInAndEndBeforeOrderByStartDesc(
                                allItemsOfOwnerIds, currentTime));
            case FUTURE:
                return BookingMapper.convertBookingToDtoOutput(bookingRepository
                        .findBookingsByItemIdInAndStartAfterOrderByStartDesc(
                                allItemsOfOwnerIds, currentTime));
            case CURRENT:
                return BookingMapper.convertBookingToDtoOutput(bookingRepository
                        .findBookingsByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(
                                allItemsOfOwnerIds, currentTime, currentTime));
            case WAITING:
                return BookingMapper.convertBookingToDtoOutput(bookingRepository
                        .findBookingsByItemIdInAndStatusOrderByStartDesc(
                                allItemsOfOwnerIds, BookingStatus.WAITING));
            case REJECTED:
                return BookingMapper.convertBookingToDtoOutput(bookingRepository
                        .findBookingsByItemIdInAndStatusOrderByStartDesc(
                                allItemsOfOwnerIds, BookingStatus.REJECTED));
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Что-то пошло не так.");
        }
    }

    private User getUserOrThrow(long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Отсутствует пользователь с id: " + id));
    }

    private Item getItemOrThrow(long id) {
        return itemRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Отсутствует предмет с id: " + id));
    }

    private Booking getBookingOrThrow(long id) {
        return bookingRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Отсутствует аренда с id: " + id));
    }
}
