package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addNewItem(Long userId, ItemDto itemDto);

    List<ItemDto> getItems(Long userId);

    ItemDto getItem(Long itemId);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    List<ItemDto> findItemForNameOrDescription(String text);

    void deleteItem(Long userId, Long itemId);
}