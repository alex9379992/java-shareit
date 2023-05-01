package ru.practicum.shareit.item.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemResponseDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private Long ownerId;
}
