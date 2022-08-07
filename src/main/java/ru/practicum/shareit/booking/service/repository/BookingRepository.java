package ru.practicum.shareit.booking.service.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findBookingsByBookerIdAndStatusOrderByStartDesc(long bookerId, BookingStatus status,
                                                                  Pageable pageable);

    List<Booking> findBookingsByBookerIdOrderByStartDesc(long bookerId, Pageable pageable);

    List<Booking> findBookingsByBookerIdAndEndBeforeOrderByStartDesc(long bookerId,
                                                                     LocalDateTime time,
                                                                     Pageable pageable);

    List<Booking> findBookingsByBookerIdAndStartAfterOrderByStartDesc(long bookerId,
                                                                      LocalDateTime time,
                                                                      Pageable pageable);

    List<Booking> findBookingsByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(long bookerId,
                                                                                  LocalDateTime time1,
                                                                                  LocalDateTime time2,
                                                                                  Pageable pageable);

    List<Booking> findBookingsByItemIdInAndStatusOrderByStartDesc(List<Long> itemIds, BookingStatus status,
                                                                  Pageable pageable);

    List<Booking> findBookingsByItemIdInOrderByStartDesc(List<Long> itemIds,
                                                         Pageable pageable);

    List<Booking> findBookingsByItemIdInAndEndBeforeOrderByStartDesc(List<Long> itemIds,
                                                                     LocalDateTime time,
                                                                     Pageable pageable);

    List<Booking> findBookingsByItemIdInAndStartAfterOrderByStartDesc(List<Long> itemIds,
                                                                      LocalDateTime time,
                                                                      Pageable pageable);

    List<Booking> findBookingsByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(List<Long> itemIds,
                                                                                  LocalDateTime time1,
                                                                                  LocalDateTime time2,
                                                                                  Pageable pageable);

    Booking findFirstBookingByItemIdAndEndBeforeOrderByStartAsc(long itemId, LocalDateTime time);

    Booking findFirstBookingByItemIdAndStartAfterOrderByStartAsc(long itemId, LocalDateTime time);

    List<Booking> findBookingsByItemIdAndBookerIdAndStatusAndStartBefore(long itemId,
                                                                         long bookerId,
                                                                         BookingStatus status,
                                                                         LocalDateTime time);
}
