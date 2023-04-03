package ru.yandex.practicum.shareIt.item;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;


@Entity
@Table(name = "items")
@Getter @Setter
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

    @Column(name = "owner_id")
    private Long owner;

    @Column(name = "request")
    private String request;
}
