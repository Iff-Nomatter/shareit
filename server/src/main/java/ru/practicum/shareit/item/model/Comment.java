package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.GenerationType.SEQUENCE;

@Data
@Entity(name = "Comment")
@Table(name = "comments")

public class Comment {
    @Id
    @SequenceGenerator(
            name = "comment_sequence",
            sequenceName = "comments_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = SEQUENCE,
            generator = "comment_sequence"
    )
    @Column(
            name = "id",
            updatable = false,
            unique = true
    )
    private long id;
    @Column(
            name = "text",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String text;
    @ManyToOne(
            cascade = CascadeType.ALL
    )
    @JoinColumn(
            name = "item_id"
    )
    private Item item;
    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "author_id"
    )
    private User author;
    @Column(
            name = "created"
    )
    private LocalDateTime created;
}
