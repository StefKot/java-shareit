package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;


@Data
public class Item {
    private Long id;
    private Long userId;
    private String name;
    private String description;
    private Boolean available;
    private String owner;
    private ItemRequest request;
}