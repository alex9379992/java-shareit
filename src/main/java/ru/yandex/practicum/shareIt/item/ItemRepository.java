package ru.yandex.practicum.shareIt.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.shareIt.item.model.Item;
import ru.yandex.practicum.shareIt.user.model.User;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwner(User owner);
    List<Item> findAllByRequestId(long requestId);

    List<Item> findByNameOrDescriptionContainingIgnoreCase(@NotEmpty String name, @NotEmpty String description);
}
