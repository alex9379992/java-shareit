package ru.yandex.practicum.shareIt.response.model;

import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.shareIt.item.model.Item;
import ru.yandex.practicum.shareIt.request.model.Request;
import ru.yandex.practicum.shareIt.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "responses")

@Getter @Setter
public class Response {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private Request request;

    @OneToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @OneToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column(name = "created")
    private LocalDateTime create;
}
