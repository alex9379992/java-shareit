package ru.yandex.practicum.shareIt.item.model;

import lombok.*;
import ru.yandex.practicum.shareIt.request.model.Request;
import ru.yandex.practicum.shareIt.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;


@Entity
@Table(name = "items")
@Getter
@Setter
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Column(name = "name", nullable = false)
    private String name;

    @NotEmpty
    @Column(name = "description")
    private String description;

    @NonNull
    @Column(name = "available")
    private Boolean available;

    @OneToOne
    @JoinColumn(name = "owner_id")
    private User owner;

}
