package ru.practicum.shareit.item.exceptions;

import lombok.Getter;
import ru.practicum.shareit.item.model.Item;

public class ItemNotFoundException extends RuntimeException {
    @Getter
    Item item;

    public ItemNotFoundException(String message) {
        super(message);
    }

    public ItemNotFoundException(String message, Item item) {
        super(message);
        this.item = item;
    }
}