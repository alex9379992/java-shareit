package ru.yandex.practicum.shareIt.item.model;

import lombok.*;
import ru.yandex.practicum.shareIt.request.model.Request;
import ru.yandex.practicum.shareIt.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Objects;


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

    @OneToOne
    @JoinColumn(name = "request_id")
    private Request request;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id) && Objects.equals(name, item.name) && Objects.equals(description, item.description) && available.equals(item.available) && Objects.equals(owner, item.owner) && Objects.equals(request, item.request);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, available, owner, request);
    }

}
