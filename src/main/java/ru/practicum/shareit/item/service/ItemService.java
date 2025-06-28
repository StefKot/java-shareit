package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item addNewItem(Long userId, Item item);

    List<Item> getItems(Long userId);

    Item getItem(Long itemId);

    Item updateItem(Long userId, Long itemId, Item item);

    List<Item> findItemForNameOrDescription(String text);

    void deleteItem(Long userId, Long itemId);
}