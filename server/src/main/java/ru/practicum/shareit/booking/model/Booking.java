package ru.practicum.shareit.booking.model;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.GenerationType.SEQUENCE;

@Data
@Entity(name = "Booking")
@Table(name = "bookings")
public class Booking {
    @Id
    @SequenceGenerator(
            name = "booking_sequence",
            sequenceName = "bookings_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = SEQUENCE,
            generator = "booking_sequence"
    )
    @Column(
            name = "id",
            updatable = false,
            unique = true
    )
    private long id;
    @Column(
            name = "start_date"
    )
    private LocalDateTime start;
    @Column(
            name = "end_date"
    )
    private LocalDateTime end;
    @OneToOne
    @JoinColumn(
            name = "item_id"
    )
    private Item item;
    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "booker_id"
    )
    private User booker;
    @Enumerated(EnumType.ORDINAL)
    @Column(
            name = "state_id"
    )
    private BookingStatus status;
}
