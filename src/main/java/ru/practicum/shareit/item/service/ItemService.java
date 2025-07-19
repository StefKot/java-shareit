package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item create(Item item, Long ownerId);

    Item update(Item item, Long ownerId);

    Item getById(Long itemId);

    List<ItemDto> getAllByOwner(Long ownerId);

    List<ItemDto> search(String text);

    void delete(Long itemId, Long ownerId);

    List<ItemDto> findAllByRequestId(Long requestId);

    ItemDto getItemDtoById(Long itemId, Long userId);

    List<ItemDto> findAllByRequestIdIn(List<Long> requestIds);
}