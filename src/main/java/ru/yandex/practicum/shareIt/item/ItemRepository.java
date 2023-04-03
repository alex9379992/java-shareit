package ru.yandex.practicum.shareIt.item;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.constraints.NotEmpty;
import java.util.List;


public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwner(long ownerId);
    List<Item> findByNameOrDescriptionContainingIgnoreCase(@NotEmpty String name, @NotEmpty String description);
}
