package ru.practicum.shareit.requests.model;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.GenerationType.SEQUENCE;

@Data
@Entity(name = "ItemRequest")
@Table(name = "item_requests")
public class ItemRequest {
    @Id
    @SequenceGenerator(
            name = "item_request_sequence",
            sequenceName = "items_requests_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = SEQUENCE,
            generator = "item_request_sequence"
    )
    @Column(
            name = "id",
            updatable = false,
            unique = true
    )
    private long id;
    @Column(
            name = "description",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String description;
    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "requester_id"
    )
    private User requestor;
    @Column(
            name = "created"
    )
    private LocalDateTime created;
}
