package ru.yandex.practicum.shareIt.item.model;

import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    Item toItem(ItemDto itemDto);

    ItemDto toItemDto(Item item);

    default List<ItemDto> toItemDtoList(List<Item> items) {
        return items.stream().filter(Item::getAvailable).map(this::toItemDto).collect(Collectors.toList());
    }
}
