package ru.yandex.practicum.shareIt.item.comment.model;

import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.shareIt.item.model.Item;
import ru.yandex.practicum.shareIt.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text")
    private String text;

    @OneToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @OneToOne
    @JoinColumn(name = "author_id")
    private User user;

    @Column(name = "created")
    private LocalDateTime created;
}
